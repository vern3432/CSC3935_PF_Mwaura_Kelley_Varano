# Real Time Protocol (RTP) Audio Streaming and Audio Library Management Application
## CSC3935_PF_Mwaura_Kelley_Varano
Final Project for CSC3935: Moses Mwaura, Aidan Varano, Sean Kelley

# Project Overview

This project involves the development of a music library management system implemented in Java. The system allows users to interact with a command-line interface (CLI) to manage songs and albums, view library contents, and start a server for media streaming. The application supports adding songs with associated album images, which are compressed and stored in a Base64 encoded format to optimize storage.

From this CLI Sever, users are able to remotely connect, select songs, and stream Music using the Real Time Protocol over a UDP connection.


## Basic Usage:
To use the application, follow these steps:
1. **Run the Driver**: Start the application by running `Driver.java`. This will initiate the CLI for server-side operations.
2. **Start the Server**: Use the `StartServer` command in the CLI to activate the server for handling client connections and media streaming.
3. **Connect the Client**: After the server is running, launch `Client.java` from a separate terminal to connect to the server and start interacting with the media library through the GUI.

## Key Features

- **Music Library Management**: Users can add songs and albums to the library. Each song can have an associated album image, which is compressed and encoded before storage.
- **CLI for Library Interaction**: The system provides a CLI for users to interact with the library, allowing them to add songs, view the library, and manage albums.

- **Media Streaming**: A server component can be started from the CLI, enabling media streaming capabilities from a Client Connection using RTP

## Technical Highlights

- **Image Compression**: Images are resized and compressed before being converted to a Base64 string to reduce the storage requirement. This is handled during the song addition process as shown below:

- **Server Initialization**: The server can be started via the CLI to facilitate media streaming, enhancing the application's utility for real-time media access.

This project combines media management with streaming technology, providing a comprehensive solution for managing and accessing media content efficiently.

# Application Usage

## CLI Usage

The Command Line Interface (CLI) allows you to manage the music library and control the server. Here are the basic commands:

- **AddSong `<song_name>`**: Adds a new song to the library. You will be prompted to select an MP3 file and an image for the album cover.
- **AddAlbum `<album_name>`**: Adds a new album. You will select a directory that contains the songs.
- **PrintLibrary**: Displays the current contents of the library.
- **StartServer**: Initializes the server for media streaming.
- **ViewLibrary**: Opens the library view where you can see all songs and albums.
- **Exit**: Closes the CLI.

To use the CLI, run the `Driver.java` and enter commands as prompted.

## Client GUI Usage
The Client GUI provides an interactive interface for managing and streaming audio content. Here's how to navigate and use the GUI:

### Main Window
- **Setup Connection**: Establishes a connection to the server for streaming.
- **Play/Pause Button**: Controls the playback of the streamed audio.
- **Tear Down**: Disconnects the streaming session.

### Album and Song Information
- **Album Cover Display**: Shows the album cover of the currently playing song.
- **Song Title Display**: Indicates the title of the currently playing song.

### Progress and Control
- **Progress Bar**: Displays the current progress of the song being played.
- **Volume Control**: Allows adjusting the volume of the playback.

To launch the GUI, run the `Client.java` from the client side after starting the server from the CLI. The GUI is designed to be user-friendly, providing straightforward options for interacting with the music library and streaming service.

## Configuration for Server and Client Objects

Each Server and Client and has it's own Configuration object, and json file associated with it. In order to use your own parameters for these objects, you can use the already existing files, or add your own files that adhere to the required format.

All formats are described in the javadocs of each respective Configuration object.

# Challenges and and Considerations, Future Enhancements, and Conclusions

## Challenges and Considerations


## Conclusion

This music library management system serves as a robust platform for managing and streaming media content. With its current features and planned enhancements, it aims to provide a comprehensive tool for media enthusiasts and professionals alike. The integration of advanced features like image compression and media streaming demonstrates the application's capability to handle modern media management challenges effectively.



The Real-time Transport Protocol (RTP) is primarily used for delivering audio and video over IP networks, such as in streaming media systems, video conferencing, and push-to-talk features. RTP operates on top of the User Datagram Protocol (UDP), providing a way to manage the real-time transmission of multimedia data. Here's a simplified breakdown of the communication flow in RTP:

Session Initiation: Before RTP packets are exchanged, a session needs to be initiated. This is typically done using the Session Initiation Protocol (SIP) or another signaling protocol which sets up the connection parameters, such as the IP addresses and port numbers for RTP (and RTCP, if used) communication.
Data Transfer:
RTP Packets: Once the session is established, the sender starts packaging multimedia data into RTP packets. Each RTP packet includes a header and a payload. The header contains information critical for the receiver to properly reconstruct the media stream, such as payload type (identifies the format of the media), sequence number (for packet order), timestamp (for synchronization), and synchronization source identifier (SSRC, which uniquely identifies the stream).
Transmission: RTP packets are sent over UDP. UDP is chosen because it is lightweight and supports the time-sensitive delivery needed for media streams, even though it does not guarantee delivery.
Control and Feedback with RTCP: Alongside RTP, the Real-time Transport Control Protocol (RTCP) is often used. RTCP is a companion protocol that allows monitoring of the data delivery and provides minimal control and identification functionalities. It works by periodically sending control packets to participants in a streaming multimedia session. The main functions of RTCP packets include providing feedback on the quality of service (QoS), carrying session control messages, and conveying information about the participants.
Media Reconstruction: At the receiver's end, RTP packets are processed as they arrive. The receiver uses the sequence numbers to re-order packets that may have arrived out of order and uses the timestamps to correct improper timing due to network delays.
Session Termination: The session can be terminated using the control protocols (like SIP) that initiated the session. RTCP can also send BYE packets to end a session when a participant leaves the stream.
This protocol setup, particularly the use of RTP combined with RTCP over UDP, effectively supports real-time multimedia streaming by managing the timing, order, and delivery of data, despite the inherent unreliability of UDP.

