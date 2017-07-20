package mysoundcloudmusicdownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import mysoundcloudmusicdownloader.ResolvedRequest.RequestURL;

/*
 * Used to download streams of music via Soundcloud
 */
public class App
{
	public static void main(String[] args) {
		try {
			//run();
			String result = acrostic(args);
			System.out.println(result);
			return;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}

    public static String acrostic(String[] args) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() > i) {
                b.append(args[i].charAt(i));
            } else {
                b.append('?');
            }
        }
        return b.toString();
    }

	static final String CLIENT_ID = "cc96dfbae2768d9291e729d107f89792";
	static final String RESOLVE_URL = "https://api.soundcloud.com/resolve.json?url=";
	static final String RESOLVED_CLIENT_ID = "?client_id=" + CLIENT_ID;
	static final String UNRESOLVED_CLIENT_ID = "&client_id=" + CLIENT_ID;
	static final String PERSONAL_USE_BACKUP_DIR = "cloudSoundPU/";
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private static void run()
	{
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				request.setParser(new JsonObjectParser(JSON_FACTORY));
			}
		});


		List<String> bunchOfUrls = new ArrayList<String>();
		for(String url : bunchOfUrls)
		{
			System.out.println();
			System.out.println("-----------------------------------------------");
			String requestUrl = createRequestUrl(url);
			System.out.println("Initial request url: " + requestUrl);
			System.out.println();
			RequestURL initialRequest = new RequestURL(requestUrl);
			HttpRequest resolveRequest;
			try {
				resolveRequest = requestFactory.buildGetRequest(initialRequest);
				ResolvedRequest resolvedRequest = resolveRequest.execute().parseAs(ResolvedRequest.class);
				resolveRequest.execute().disconnect();
				System.out.println("---Initial request response---");
				System.out.println("Kind: " + resolvedRequest.kind);
				System.out.println("ID: " + resolvedRequest.id);
				System.out.println("URI: " + resolvedRequest.uri);
				System.out.println("---Initial request response---");
				System.out.println();
				CloudSoundGetter cloudSound = new CloudSoundGetter(resolvedRequest.uri + RESOLVED_CLIENT_ID, resolvedRequest.kind);
				HttpRequest request = requestFactory.buildGetRequest(cloudSound);
				cloudSound.readResponse(request.execute());				
				if(cloudSound.isPlaylist())
				{
					Playlist playlist = cloudSound.getPlaylist();
					System.out.println("Playlist Title: " + playlist.getTitle());
					if(playlist.getTracks() != null)
					{
						String playlistDirectory = playlist.getTitle() + "/";
						for(Track track : playlist.getTracks())
						{
							System.out.println("Track title: " + track.title);
							
							createAndTagTrack(track, playlistDirectory);
						}
					}
				}
				else if (cloudSound.isTrack()) {
					Track track = cloudSound.getTrack();
					createAndTagTrack(track);
				}
				System.out.println("---Finished processing Initial request response---");
				System.out.println("Kind: " + resolvedRequest.kind);
				System.out.println("ID: " + resolvedRequest.id);
				System.out.println("URI: " + resolvedRequest.uri);
				System.out.println("---Finished processing Initial request response---");
				System.out.println();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
			System.out.println("Done!");
			System.out.println();
		}
	}

	/**
	 * Used to tag and create individual tracks for personal use.
	 *
	 * @param track - Track to be created, tagged and available for personal use.
	 */
	private static void createAndTagTrack(Track track) {
		createAndTagTrack(track, "");
	}

	/**
	 * Used to tag and create individual Tracks for personal use.
	 *
	 * @param track - Track to be created, tagged and available for personal use.
	 * @param directory - For storing track in a specific directory.
	 *	Example would be to store tracks from a single playlist in a directory
	 *	named after the playlist title.
	 */
	private static void createAndTagTrack(Track track, String directory)
	{
		System.out.println();
		System.out.println("--CREATING AND TAGGING TRACK--");
		System.out.println();
		String finalDirectory = PERSONAL_USE_BACKUP_DIR;
		String tempDirectory = PERSONAL_USE_BACKUP_DIR + "tmp/";
		if(!directory.isEmpty()) //is not empty
			finalDirectory = finalDirectory + directory;

		//create tag with 'track' json post
		ID3v2 id3v2Tag = createID3v2Tag(track);

		//streamUrl and the resolvedClientId
		// we can generate a Url to the stream for download
		String streamableCSUrl = track.stream_url + RESOLVED_CLIENT_ID;
		System.out.println("Streamable CloudSound Url: " + streamableCSUrl);
		URL cloudSoundDL;
		try {
			cloudSoundDL = new URL(streamableCSUrl);

			//using the generated StreamURL read the resulting request to a byte array
			byte[] result = inputStreamToByteArray(cloudSoundDL.openStream());
			System.out.println("----Personal Use Backup Completed----");

			//TODO(eric.calvano): This filename generator needs to be sorted out ASAP;
			//	Will require checks against artist name to remove redundancies

			//create file name
			String fileName = track.title.trim() + ".mp3";
			Pattern p = Pattern.compile("[*]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(fileName);
			boolean hasSpecialCharacter = m.find();
			if (hasSpecialCharacter)
			{
				   System.out.println("There is a special character in my string");
				   fileName=track.user.trim() + ".mp3";
			}
			Pattern pt = Pattern.compile("//");
			Matcher match = pt.matcher(fileName);
			while (match.find()) {
				System.out.println("Begin removal of " + "//" + " in fileName: " + fileName);
				fileName = fileName.replaceAll("//", "");
				System.out.println("Removal of " + "//" + " successful; New fileName: " + fileName);
			}

			//create directory location
			File personalUseDirectory = new File(finalDirectory + fileName);
			personalUseDirectory.getParentFile().mkdirs();

			//create temp directory location
			File temp = new File(tempDirectory + fileName);
			temp.getParentFile().mkdirs();

			System.out.println("Temp Save location -----" + tempDirectory);

			//using the resulting byte array
			// create a MP3File
			Mp3File mp3file = createMp3File(result, temp);
	
			if(mp3file != null)
			{
				//tag with id3v2Tag and save
				tagAndSave(id3v2Tag, mp3file, personalUseDirectory);
			}
			else
			{
				System.out.print("[ERROR]--- mp3File was null.");
				System.out.println();
				System.out.println();
			}
			temp.delete();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String createRequestUrl(String url) {
		String scUrlWithUnresolvedClientID = url + UNRESOLVED_CLIENT_ID;
		String finalUrl = RESOLVE_URL + scUrlWithUnresolvedClientID;
		return finalUrl;
	}

	public static byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
		System.out.println("----Attempting Download----");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int bytesRead;
		while ((bytesRead = inStream.read(buffer)) > 0) {
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	private static ID3v2 createID3v2Tag(Track track)
	{
		System.out.println("----Creating ID3v2 Tag----");
		ID3v2 id3v2Tag = new ID3v24Tag();
		if(track.title != null)
		{
			System.out.println("Title: " + track.title);
			id3v2Tag.setTitle(track.title);
		}
		if(track.user != null)
		{
			//id3v2Tag.setArtist(arg0);
		}
		if(track.genre != null)
		{
			System.out.println("Genre: " + track.genre);
			int genre = ID3v1Genres.matchGenreDescription(track.genre);
			if(genre != -1)
			{
				System.out.println("ID3v2 Genre: " + genre);
				id3v2Tag.setGenre(genre);
			}
			else	//error out on genre
				id3v2Tag.setComment(track.genre); // if genre is not found, append genre to description.
		}
		//id3v2Tag.setYear(arg0);
		//id3v2Tag.setTrack(arg0);

		// Will be the most difficult considering not all will contain an "album"
		//id3v2Tag.setAlbum(arg0);
		System.out.println("Stream_Url: " + track.stream_url);
		System.out.println("Permalink_Url: " + track.permalink_url);
		System.out.println("Is downloadable: " + track.downloadable);
		if (track.downloadable)
		{
			System.out.println("Download Link: " + track.download_url);
		}
		System.out.println("----Finished creating ID3v1 Tag----");
		System.out.println();
		return id3v2Tag;
	}

	private static Mp3File createMp3File(byte[] result, File tempFile)
	{
		System.out.println("----Creating Mp3 File----");
		System.out.println("~FileName: " + tempFile.getName().trim());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(tempFile);
			System.out.println("----Write result to Mp3 File----");
			fos.write(result);
			fos.close();
			System.out.println("----Finished creating Mp3 File----");
			System.out.println("----Opening File as Mp3File type----");
			return new Mp3File(tempFile);
		} catch (UnsupportedTagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void tagAndSave(ID3v2 id3v2Tag, Mp3File mp3file, File finalFile)
			throws IOException, NotSupportedException {
		System.out.println("----Setting ID3v1 tag to Mp3File----");
		mp3file.setId3v2Tag(id3v2Tag);

		//save mp3 to updatedDirectory with same filename
		System.out.println("----Saving updated Mp3File: " + finalFile + "----");
		mp3file.save(finalFile.getAbsolutePath());
		System.out.println("----Update complete of Mp3File: " + finalFile.getAbsolutePath() + "----");
		
		//TODO: Move into test and write unit test to verify tag data.
		// for now use console out for verification.
		verifyTagData(mp3file);
	}

	//turn into unit test
	private static void verifyTagData(Mp3File mp3file)
			throws IOException, NotSupportedException {
		System.out.println();
		System.out.println("----Verifying tag data of Mp3File----");
		ID3v2 id3v2TagTest =  mp3file.getId3v2Tag();
		System.out.println("[id3v2TagTest] Title: " + id3v2TagTest.getTitle());
		System.out.println("[id3v2TagTest] Genre: " + id3v2TagTest.getGenre());
	}

}
