import socket


class util:
    def __init__(self) -> None:
        pass

    def get_ipaddress():
        hostname = socket.gethostname()
        # Get the IP address of the system
        ip_address = socket.gethostbyname(hostname)
        return ip_address
    

    def get_port_number():
    # Create a socket object
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
         # Bind to a random available port
             sock.bind(('localhost', 0))
            # Get the socket's port number
             _, port = sock.getsockname()
             return port
        finally:
        # Close the socket
            sock.close()
    

    def close_port(port):
        try:
            # Create a socket object
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
            # Bind to the specified port
            sock.bind(('localhost', port))
        
            # Close the socket
            sock.close()
            # print(f"Port {port} closed.")
        except Exception as e:
            pass
            # print(f"Error closing port {port}: {str(e)}")






if __name__=='__main__':
    # print(util.get_ipaddress())
    # print(util.get_port_number())
    util.close_port(util.get_port_number())

