package mysoundcloudmusicdownloader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

public class App
{
	public static void main(String[] args) {
		try {
			run();
			return;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}

	// List of tested and verified tracks
	//static final String SC_URL = "https://soundcloud.com/burnitdownremixcontest/lny-tnz-burn-it-down-filament-remix";
	//static final String SC_URL = "https://soundcloud.com/hytydremixs/cash-cash-millionaire-ft-nelly-hytyd-remix";
	//static final String SC_URL = "https://soundcloud.com/destroyed-by-seek-n-destroy/seek-n-destroy-edits-volume-1";
	//static final String SC_URL = "https://soundcloud.com/spinnin-deep/raving-george-feat-oscar-and-the-wolf-youre-mine-original-mix-available-august-24";
	//static final String SC_URL = "https://soundcloud.com/ludomir/closingin";
	static final String SC_URL = "https://soundcloud.com/emdmusic/gale-song-lumineers-cover";
	static final String CLIENT_ID = "";
	static final String RESOLVE_URL = "https://api.soundcloud.com/resolve.json?url=";
	static final String RESOLVED_CLIENT_ID = "?client_id=" + CLIENT_ID;
	static final String UNRESOLVED_CLIENT_ID = "&client_id=" + CLIENT_ID;
	static final String TAGGED_DIRECTORY = "tagged/";
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private static void run()
	{
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				request.setParser(new JsonObjectParser(JSON_FACTORY));
			}
		});
		System.out.println("-----------------------------------------------");
		String requestUrl = createRequestUrl();
		System.out.println("Initial request url: " + requestUrl);
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

			CloudSoundGetter cloudSound = new CloudSoundGetter(resolvedRequest.uri + RESOLVED_CLIENT_ID, resolvedRequest.kind);
			HttpRequest request = requestFactory.buildGetRequest(cloudSound);
			cloudSound.readResponse(request.execute());
	
			if(cloudSound.isPlaylist())
			{
				
			}
			else if (cloudSound.isTrack()) {
				createAndTagTrack(cloudSound);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("Done!");
	}

	private static void createAndTagTrack(CloudSoundGetter cloudSound) {
		Track track = cloudSound.getTrack();
		//create tag with 'track' json post
		ID3v2 id3v2Tag = createID3v2Tag(track);
		System.out.println("----Finished creating ID3v1 Tag----");
		System.out.println();

		//using the streamURL and the resolvedClientID
		// we can generate a URL to the stream for download
		String streamableCSUrl = track.stream_url + RESOLVED_CLIENT_ID;
		System.out.println("Streamable CloudSound Url: " + streamableCSUrl);
		URL cloudSoundDL;
		try {
			cloudSoundDL = new URL(streamableCSUrl);

			//using the generated StreamURL read the resulting request to a byte array
			byte[] result = inputStreamToByteArray(cloudSoundDL.openStream());
			System.out.println("----Download Completed----");
	
			//create file name
			String fileName = track.title + ".mp3";
			//using the resulting byte array
			// create a MP3File
			Mp3File mp3file = createMp3File(result, fileName);
	
			if(mp3file != null)
			{
				//tag with id3v2Tag and save
				tagAndSave(id3v2Tag, mp3file);
			}
			else
				System.out.print("[ERROR]--- mp3File was null.");
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

	private static String createRequestUrl() {
		String scUrlWithUnresolvedClientID = SC_URL + UNRESOLVED_CLIENT_ID;
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
		return id3v2Tag;
	}

	private static Mp3File createMp3File(byte[] result, String fileName)
	{
		System.out.println("----Creating Mp3 File----");
		System.out.println("~FileName: " + fileName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileName);
			System.out.println("----Write result to Mp3 File----");
			fos.write(result);
			fos.close();
			System.out.println("----Finished creating Mp3 File----");
			System.out.println("----Opening File as Mp3File type----");
			return new Mp3File(fileName);
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

	private static void tagAndSave(ID3v2 id3v2Tag, Mp3File mp3file)
			throws IOException, NotSupportedException {
		System.out.println("----Setting ID3v1 tag to Mp3File----");
		mp3file.setId3v2Tag(id3v2Tag);

		//save mp3 to updatedDirectory with same filename
		System.out.println("----Saving updated Mp3File: " + mp3file.getFilename() + "----");
		mp3file.save(TAGGED_DIRECTORY + mp3file.getFilename());
		System.out.println("----Update complete of Mp3File: " + mp3file.getFilename() + "----");
		
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
