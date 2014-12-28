package com.forgeessentials.remote.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;

import com.forgeessentials.remote.client.RemoteResponse.JsonRemoteResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class RemoteClient implements Runnable {

    public static final String SEPARATOR = "\n\n\n";

    private static String certificateFilename = "FeRemotePub.jks";
    private static String certificatePassword = "feremote";

    public final Socket socket;

    private final Thread thread;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private int currentRid = 1;

    private Set<Integer> waitingRids = new HashSet<Integer>(); // Collections.synchronizedSet(new HashSet<Integer>());

    private List<JsonRemoteResponse> responses = Collections.synchronizedList(new LinkedList<JsonRemoteResponse>());

    // ------------------------------------------------------------

    public RemoteClient(Socket socket) throws UnknownHostException, IOException
    {
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public RemoteClient(String host, int port) throws UnknownHostException, IOException
    {
        this(new Socket(host, port));
    }

    public RemoteClient(String host, int port, SSLContext sstCtx) throws UnknownHostException, IOException
    {
        this(sstCtx.getSocketFactory().createSocket(host, port));
    }

    public Gson getGson()
    {
        return gson;
    }

    // ------------------------------------------------------------

    public static RemoteClient createSslClient(String host, int port)
    {
        try
        {
            InputStream is = ClassLoader.getSystemResourceAsStream(certificateFilename);
            if (is != null)
            {
                SSLContextHelper sslCtxHelper = new SSLContextHelper();
                sslCtxHelper.loadSSLCertificate(is, certificatePassword, certificatePassword);
                return new RemoteClient("localhost", 27020, sslCtxHelper.getSSLCtx());
            }
            else
                throw new RuntimeException("[remote] Unable to load SSL certificate: File not found");
        }
        catch (IOException | GeneralSecurityException e1)
        {
            throw new RuntimeException("[remote] Unable to load SSL certificate: " + e1.getMessage());
        }
    }

    /**
     * Main message loop
     */
    @Override
    public void run()
    {
        try
        {
            final SocketStreamSplitter sss = new SocketStreamSplitter(socket.getInputStream(), SEPARATOR);
            while (true)
            {
                try
                {
                    final String msg = sss.readNext();
                    if (msg == null)
                    {
                        // OutputHandler.felog.warning("[remote] Connection closed: " + getRemoteHostname());
                        break;
                    }
                    processMessage(msg);
                }
                catch (IOException e)
                {
                    // OutputHandler.felog.warning("[remote] Socket error: " + e.getMessage());
                    break;
                }
            }
        }
        catch (IOException e)
        {
            // OutputHandler.felog.warning("[remote] Error opening input stream.");
        }

        // Be sure to close the thread
        close();

        // Notify all waiting threads so they can notice the socket got closed
        synchronized (this)
        {
            notifyAll();
        }
    }

    /**
     * All received messages start being processed here
     * 
     * @param message
     * @throws IOException
     */
    protected synchronized void processMessage(String message) throws IOException
    {
        try
        {
            JsonRemoteResponse response = gson.fromJson(message, JsonRemoteResponse.class);

            // Check, if too many messages piled up
            if (responses.size() > 32)
                responses.remove(0);

            // Add response to last responses and notify all waiting threads
            responses.add(response);
            notifyAll();
        }
        catch (IllegalArgumentException e)
        {
            // OutputHandler.felog.warning("[remote] Message error: " + e.getMessage());
            return;
        }
        catch (JsonSyntaxException e)
        {
            // OutputHandler.felog.warning("[remote] Message error: " + e.getMessage());
            return;
        }
    }

    /**
     * Wait for a response. If rid is null, it waits for any message that is not already waited for.
     * 
     * @param rid
     * @param timeout
     */
    protected synchronized JsonRemoteResponse waitForResponse(Integer rid, int timeout)
    {
        // Remember start-time
        long startTime = System.currentTimeMillis();
        try
        {
            while (true)
            {
                // Check if the socket was closed
                if (isClosed())
                    return null;

                // Wait for response to arrive and check for timeout
                if (timeout > 0)
                {
                    long t = startTime + timeout - System.currentTimeMillis();
                    wait(Math.max(1, t));
                    if (t <= 0)
                        return null;
                }
                else
                    wait();

                Iterator<JsonRemoteResponse> it = responses.iterator();
                while (it.hasNext())
                {
                    JsonRemoteResponse response = it.next();
                    if (rid == null && !waitingRids.contains(response.rid) || rid != null && response.rid == rid)
                    {
                        // Remove the response from the queue
                        it.remove();
                        return response;
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            return null;
        }
    }

    /**
     * Send request to server
     * 
     * @param request
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public synchronized void sendRequest(RemoteRequest request) throws IOException
    {
        if (isClosed())
            return;
        request.rid = currentRid++;
        OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream());
        ow.write(gson.toJson(request) + SEPARATOR);
        ow.flush();
    }

    /**
     * Send a request and wait for the response
     * 
     * 
     * @param request
     * @param clazz
     * @param timeout
     * @throws IOException
     */
    public synchronized JsonRemoteResponse sendRequestAndWait(RemoteRequest<?> request, int timeout)
    {
        try
        {
            if (isClosed())
                return null;
            sendRequest(request);
            waitingRids.add(request.rid);
            return waitForResponse(request.rid, timeout);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    /**
     * Send a request and wait for the response
     * 
     * 
     * @param request
     * @param clazz
     * @param timeout
     * @throws IOException
     */
    public JsonRemoteResponse sendRequestAndWait(RemoteRequest<?> request)
    {
        return sendRequestAndWait(request, 0);
    }

    /**
     * Send a request and wait for the response
     * 
     * 
     * @param request
     * @param clazz
     * @param timeout
     * @throws IOException
     */
    public <T> RemoteResponse<T> sendRequestAndWait(RemoteRequest<?> request, Class<T> clazz, int timeout)
    {
        JsonRemoteResponse response = sendRequestAndWait(request, timeout);
        if (response == null)
            return null;
        // Deserialize the data payload now that we know it's type
        return transformResponse(response, clazz);
    }

    /**
     * Send a request and wait for the response
     * 
     * 
     * @param request
     * @param clazz
     * @param timeout
     * @throws IOException
     */
    public <T> RemoteResponse<T> sendRequestAndWait(RemoteRequest<?> request, Class<T> clazz)
    {
        return sendRequestAndWait(request, clazz, 0);
    }

    /**
     * Transforms a generic response into one with the correctly deserialized data
     * 
     * @param response
     * @param clazz
     * @return
     */
    public <T> RemoteResponse<T> transformResponse(JsonRemoteResponse response, Class<T> clazz)
    {
        return RemoteResponse.transform(response, gson.fromJson(response.data, clazz));
    }

    /**
     * Get the next response, that is not waited for.
     * 
     * @param timeout
     */
    public JsonRemoteResponse getNextResponse(int timeout)
    {
        return waitForResponse(null, timeout);
    }

    /**
     * Checks, if the connection was closed
     */
    public boolean isClosed()
    {
        return socket.isClosed();
    }

    /**
     * Terminates the session
     */
    public void close()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            /* ignore */
        }
    }

}
