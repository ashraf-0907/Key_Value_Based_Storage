# FileOutputStrem 
1) A file output stream is an output stream for writing data to a File or to a FileDescriptor.
------
# FileInputStream
1) A FileInputStream obtains input bytes from a file in a file system. What files are available depends on the host environment.
2) FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
3) constructor :- FileInputStream(File file) OR FileInputStream(FileDescriptor fdObj);
----- 
# Socket and its constructors
1) A server runs on a specific computer and has a socket that is bound to a specific port number. The server just waits, listening to the socket for a client to make a connection request.
2) The client knows the hostname of the machine on which the server is running and the port number on which the server is listening. To make a connection request, the client tries to rendezvous with the server on the server's machine and port.
3) Def:- A socket is one endpoint of a two-way communication link between two programs running on the network. A socket is bound to a port number so that the TCP layer can identify the application that data is destined to be sent to.
4) Socket sc = new Socket() // created an unconnected socket 
5) Socket sc = new Socket (InetAddress addr,int port) // created a stream socket and connect it to the specified port no.
6) Socket(InetAddress address, int port, InetAddress localAddr, int localPort) // Creates a socket and connects it to the specified remote address on the specified remote port.
----
# Socket and its functions 
1) void bind(SocketAddress bindpoint) //Binds the socket to a local address.
2) void connect(SocketAddress endpoint) // connects this socket to the server 
3) connect(SocketAddress endpoint, int timeout) //Connects this socket to the server with a specified timeout value.
4) InetAddress serveraddr = sc.getInetAddress() //Returns the address to which the socket is connected.
5) InputStream input = sc.getInputStream() //Returns an input stream for this socket.
6) OutputStream out = sc.getOutputStream() //Returns an output stream for the socket.
----
