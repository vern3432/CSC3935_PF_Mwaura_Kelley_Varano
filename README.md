# CSC3935_PF_Mwaura_Kelley_Varano
Final Project for CSC3935: Moses Mwaura, Aidan Varano, Sean Kelley




Todo:

1. Make song legnth send in json and update to RTPCLIENT for progress bar caclualtion (Hardcoded rn), make it auto close the player when this works
2. Make sure proccess of LibraryView doenst close when RTP Client Does  
3. Impliment mp3 conversion proccess in the case of an mp3 file uploaded 
4. Decide whether to use relative or hard paths in json. (Its hard rn)
5. Properly Impliment Pause feature(It stops it but does not let it resume)
6. Make it so that once a song is done or closed, a new RTP stream cna be init
7. Add Album Covers to Audio Player
8. Add Song Title to Audio Player 




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















