# Real Time Protocol (RTP) Audio Streaming and Audio Library Management Application
## CSC3935_PF_Mwaura_Kelley_Varano
Final Project for CSC3935: Moses Mwaura, Aidan Varano, Sean Kelley




Todo:

1. Implement RTCP(If we feel its needed)
2.  Make song legnth send in json and update to RTPCLIENT for progress bar caclualtion (Hardcoded rn), make it auto close the player when this works
3. Make sure proccess of LibraryView doenst close when RTP Client Does  
4. Impliment mp3 conversion proccess in the case of an mp3 file uploaded 
5. Decide whether to use relative or hard paths in json. (Its hard rn)
6. Properly Impliment Pause feature(It stops it but does not let it resume)
7. Make it so that once a song is done or closed, a new RTP stream cna be init
8. Add Album Covers to Audio Player
9. Add Song Title to Audio Player 




}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
# Project Overview

This project involves the development of a music library management system implemented in Java. The system allows users to interact with a command-line interface (CLI) to manage songs and albums, view library contents, and start a server for media streaming. The application supports adding songs with associated album images, which are compressed and stored in a Base64 encoded format to optimize storage.

## Key Features

- **Music Library Management**: Users can add songs and albums to the library. Each song can have an associated album image, which is compressed and encoded before storage.
- **CLI for Library Interaction**: The system provides a CLI for users to interact with the library, allowing them to add songs, view the library, and manage albums.

- **Media Streaming**: A server component can be started from the CLI, enabling media streaming capabilities from a Client Connection using RTP

## Technical Highlights

- **Image Compression**: Images are resized and compressed before being converted to a Base64 string to reduce the storage requirement. This is handled during the song addition process as shown below:

- **Server Initialization**: The server can be started via the CLI to facilitate media streaming, enhancing the application's utility for real-time media access.

This project combines media management with streaming technology, providing a comprehensive solution for managing and accessing media content efficiently.

# Using the CLI and Client GUI

## CLI Usage

The Command Line Interface (CLI) allows you to manage the music library and control the server. Here are the basic commands:

- **AddSong `<song_name>`**: Adds a new song to the library. You will be prompted to select an MP3 file and an image for the album cover.
- **AddAlbum `<album_name>`**: Adds a new album. You will select a directory that contains the songs.
- **PrintLibrary**: Displays the current contents of the library.
- **StartServer**: Initializes the server for media streaming.
- **ViewLibrary**: Opens the library view where you can see all songs and albums.
- **Exit**: Closes the CLI.

To use the CLI, run the `MusicLibraryCLI.java` and enter commands as prompted.

## Client GUI Usage

The Client GUI provides a visual interface to interact with the music library:

- **Setup Connection**: Establishes a connection to the server.
- **Play**: Plays the selected song.
- **Pause**: Pauses the current song.
- **Close Connection**: Closes the streaming connection.
- **Describe Stream**: Provides details about the current stream.

To launch the GUI, run the `LibraryView.java` from the client side after starting the server from the CLI.

Both interfaces are designed to be intuitive, allowing easy management and access to your music library.




## Future Enhancements

- **Progress Bar for Song Playback**: Implement functionality to send song length in the JSON object to the RTP client for accurate progress bar calculations and automatic player closure upon song completion.
- **Stability Improvements**: Ensure that the process of `LibraryView` does not terminate when the RTP Client does, enhancing the robustness of the application.
- **MP3 Conversion Process**: Implement a process to handle the conversion of different audio file formats to MP3, ensuring compatibility across various devices and platforms.
- **Path Handling**: Decide on using either relative or absolute paths in the JSON storage format to optimize portability and ease of configuration.
- **Pause and Resume Feature**: Properly implement the pause feature to allow pausing and resuming of song playback, improving user interaction with the media player.


## Challenges and Considerations

- **Handling Large Files**: Managing large media files and ensuring efficient transmission over the network can be challenging. Implementing streaming protocols effectively addresses this.
- **User Interface**: While the current CLI provides basic functionality, future versions could include a graphical user interface (GUI) for enhanced user experience.
- **Security**: As the system involves file handling and network communication, ensuring data security and preventing unauthorized access is crucial.

## Conclusion

This music library management system serves as a robust platform for managing and streaming media content. With its current features and planned enhancements, it aims to provide a comprehensive tool for media enthusiasts and professionals alike. The integration of advanced features like image compression and media streaming demonstrates the application's capability to handle modern media management challenges effectively.







The Real-time Transport Protocol (RTP) is primarily used for delivering audio and video over IP networks, such as in streaming media systems, video conferencing, and push-to-talk features. RTP operates on top of the User Datagram Protocol (UDP), providing a way to manage the real-time transmission of multimedia data. Here’s a simplified breakdown of the communication flow in RTP:

Session Initiation: Before RTP packets are exchanged, a session needs to be initiated. This is typically done using the Session Initiation Protocol (SIP) or another signaling protocol which sets up the connection parameters, such as the IP addresses and port numbers for RTP (and RTCP, if used) communication.
Data Transfer:
RTP Packets: Once the session is established, the sender starts packaging multimedia data into RTP packets. Each RTP packet includes a header and a payload. The header contains information critical for the receiver to properly reconstruct the media stream, such as payload type (identifies the format of the media), sequence number (for packet order), timestamp (for synchronization), and synchronization source identifier (SSRC, which uniquely identifies the stream).
Transmission: RTP packets are sent over UDP. UDP is chosen because it is lightweight and supports the time-sensitive delivery needed for media streams, even though it does not guarantee delivery.
Control and Feedback with RTCP: Alongside RTP, the Real-time Transport Control Protocol (RTCP) is often used. RTCP is a companion protocol that allows monitoring of the data delivery and provides minimal control and identification functionalities. It works by periodically sending control packets to participants in a streaming multimedia session. The main functions of RTCP packets include providing feedback on the quality of service (QoS), carrying session control messages, and conveying information about the participants.
Media Reconstruction: At the receiver's end, RTP packets are processed as they arrive. The receiver uses the sequence numbers to re-order packets that may have arrived out of order and uses the timestamps to correct improper timing due to network delays.
Session Termination: The session can be terminated using the control protocols (like SIP) that initiated the session. RTCP can also send BYE packets to end a session when a participant leaves the stream.
This protocol setup, particularly the use of RTP combined with RTCP over UDP, effectively supports real-time multimedia streaming by managing the timing, order, and delivery of data, despite the inherent unreliability of UDP.















