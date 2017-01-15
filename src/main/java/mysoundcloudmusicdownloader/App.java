package mysoundcloudmusicdownloader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class App
{
	public static void main(String[] args) {
		try {
			try {
				run();
				return;
			} catch (HttpResponseException e) {
				System.err.println(e.getMessage());
			}
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
	static final String SC_URL = "https://soundcloud.com/ludomir/closingin";
	static final String CLIENT_ID = "";
	static final String RESOLVE_URL = "https://api.soundcloud.com/resolve.json?url=";
	static final String RESOLVED_CLIENT_ID = "?client_id=" + CLIENT_ID;
	static final String UNRESOLVED_CLIENT_ID = "&client_id=" + CLIENT_ID;
	static final String TAGGED_DIRECTORY = "tagged/";
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static class SoundCloudUrl extends GenericUrl {

		public String fields;

		private JsonRequestType jsonRequestType;

		// If json resolve stops working decoded urls the following will encode
		// the url for json to resolve
		// URLEncoder.encode("https://soundcloud.com/hytydremixs/cash-cash-millionaire-ft-nelly-hytyd-remix",
		// java.nio.charset.StandardCharsets.UTF_8.toString());
		public SoundCloudUrl(String requestUrl, JsonRequestType requestType)
		{
			super(requestUrl);
			this.setJsonRequestTypeAndFields(requestType);
		}

		public JsonRequestType getJsonRequestType() {
			return jsonRequestType;
		}

		/**
		 * Passing a requestType will set the fields for json request.
		 * @param requestType
		 */
		private void setJsonRequestTypeAndFields(JsonRequestType jsonRequestType) {
			this.jsonRequestType = jsonRequestType;
			//TODO: Move this into the individual classes so each class has it's own set of fields.
			// This implementation will not age well.
			switch (jsonRequestType) {
			case TRACK:
				//Fields for tracks
				this.fields = "kind,id,created_at,user_id,user,title,permalink,permalink_url,uri,sharing,embeddable_by,purchase_url,artwork_url,description,label,duration,genre,label_id,label_name,release,release_day,release_month,release_year,streamable,downloadable,state,license,track_type,waveform_url,download_url,stream_url,video_url,bpm,commentable,isrc,key_signature,original_format,original_content_size,asset_data,artwork_data,user_favorite";
				break;
			default:
				System.out.println("You must add the request type to the JsonRequestType enum for processing.");
				System.out.close();
				break;
			}
		};
	}

	/**
	 * JSON request type; Used with SoundCloudURL.
	 * @author eric
	 *
	 */
	public enum JsonRequestType {
		TRACK
	}

	private static void run() throws Exception
	{
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				request.setParser(new JsonObjectParser(JSON_FACTORY));
			}
		});
		System.out.println("-----------------------------------------------");
		String requestUrl = createRequestUrl();
		System.out.println("Request Url: " + requestUrl);
		SoundCloudUrl soundCloudUrl = new SoundCloudUrl(requestUrl, JsonRequestType.TRACK);
		System.out.println("Building get request");
		HttpRequest request = requestFactory.buildGetRequest(soundCloudUrl);
		System.out.println("Begin execution of built request");
		Track track = request.execute().parseAs(Track.class);
		System.out.println("Request received successfully!");
		System.out.println("Request Type: " + track.kind);
		System.out.println("Api Track ID: " + track.id);

		//create tag with 'track' json post
		ID3v1 id3v1Tag = createID3v1Tag(track);
		System.out.println("----Finished creating ID3v1 Tag----");
		//using the streamURL and the resolvedClientID
		// we can generate a URL to the stream for download
		String streamableCSUrl = track.stream_url + RESOLVED_CLIENT_ID;
		System.out.println("Streamable CloudSound Url: " + streamableCSUrl);
		URL cloudSoundDL = new URL(streamableCSUrl);

		//using the generated StreamURL read the resulting request to a byte array
		byte[] result = inputStreamToByteArray(cloudSoundDL.openStream());
		System.out.println("----Download Completed----");

		//create file name
		String fileName = track.title + ".mp3";

		//using the resulting byte array
		// create a MP3File
		Mp3File mp3file = createMp3File(result, fileName);

		//tag with id3v1Tag and save
		tagAndSave(id3v1Tag, mp3file);

		System.out.println();
		System.out.println("Done!");
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

	private static ID3v1 createID3v1Tag(Track track)
			throws IOException, UnsupportedTagException, InvalidDataException, NotSupportedException {
		System.out.println("----Creating ID3v1 Tag----");
		ID3v1 id3v1Tag = new ID3v1Tag();
		System.out.println("Title: " + track.title);
		id3v1Tag.setTitle(track.title);
		System.out.println("Stream_Url: " + track.stream_url);
		System.out.println("Permalink_Url: " + track.permalink_url);
		System.out.println("Is downloadable: " + track.downloadable);
		if (track.downloadable)
		{
			System.out.println("Download Link: " + track.download_url);
		}
		return id3v1Tag;
	}

	private static Mp3File createMp3File(byte[] result, String fileTitle)
			throws FileNotFoundException, IOException, UnsupportedTagException, InvalidDataException {
		System.out.println("----Creating Mp3 File----");
		FileOutputStream fos = new FileOutputStream(fileTitle);
		try {
			System.out.println("----Write result to Mp3 File----");
			fos.write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
			System.out.println("----Finished creating Mp3 File----");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----Opening File as Mp3File type----");
		return new Mp3File(fileTitle);
	}

	private static void tagAndSave(ID3v1 id3v1Tag, Mp3File mp3file)
			throws IOException, NotSupportedException {
		System.out.println("----Setting ID3v1 tag to Mp3File----");
		mp3file.setId3v1Tag(id3v1Tag);

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
		ID3v1 id3v1TagTest =  mp3file.getId3v1Tag();
		System.out.println("[id3v1TagTest] Title: " + id3v1TagTest.getTitle());
		System.out.println();
	}
}
