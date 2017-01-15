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

	static final String CLIENT_ID = "";
	static final String RESOLVE_URL = "https://api.soundcloud.com/resolve.json?url=";
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static class SoundCloudURL extends GenericUrl {
		// If json resolve stops working decoded urls the following will encode
		// the url for json to resolve
		// URLEncoder.encode("https://soundcloud.com/hytydremixs/cash-cash-millionaire-ft-nelly-hytyd-remix",
		// java.nio.charset.StandardCharsets.UTF_8.toString());
		public SoundCloudURL(String encodedUrl)
		{
			super(encodedUrl);
		}

		public String fields;

		private JsonRequestType jsonRequestType;

		/**
		 * Passing a requestType will set the fields for json request.
		 * @param requestType
		 */
		public void setFieldsForJsonRequest(JsonRequestType requestType)
		{
			this.setJsonRequestType(requestType);
			//TODO: Move this into the individual classes so each class has it's own set of fields.
			// This implementation will not age well.
			switch (requestType) {
			case TRACK:
				//Fields for tracks
				this.fields = "kind,id,created_at,user_id,user,title,permalink,permalink_url,uri,sharing,embeddable_by,purchase_url,artwork_url,description,label,duration,genre,label_id,label_name,release,release_day,release_month,release_year,streamable,downloadable,state,license,track_type,waveform_url,download_url,stream_url,video_url,bpm,commentable,isrc,key_signature,original_format,original_content_size,asset_data,artwork_data,user_favorite";
				break;
			}
		}

		public JsonRequestType getJsonRequestType() {
			return jsonRequestType;
		}

		private void setJsonRequestType(JsonRequestType jsonRequestType) {
			this.jsonRequestType = jsonRequestType;
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

		/*
		 * Beginning of track test, uncomment and replace url of tracks with the playlists url
		 */
		// List of tested and verified tracks
			//https://soundcloud.com/burnitdownremixcontest/lny-tnz-burn-it-down-filament-remix
			//https://soundcloud.com/hytydremixs/cash-cash-millionaire-ft-nelly-hytyd-remix
			//https://soundcloud.com/destroyed-by-seek-n-destroy/seek-n-destroy-edits-volume-1
			//https://soundcloud.com/spinnin-deep/raving-george-feat-oscar-and-the-wolf-youre-mine-original-mix-available-august-24
			//https://soundcloud.com/user-579308838/we-are-loud-feat-ida-stay-high
		String url = RESOLVE_URL + "https://soundcloud.com/user-579308838/we-are-loud-feat-ida-stay-high"
				+ "&client_id=" + CLIENT_ID;

		System.out.println();
		System.out.println("-----------------------------------------------");
		System.out.println("requestURL: " + url);
		SoundCloudURL soundcloudUrl = new SoundCloudURL(url);
		soundcloudUrl.setJsonRequestType(JsonRequestType.TRACK);

		HttpRequest request = requestFactory.buildGetRequest(soundcloudUrl);
		Track track = request.execute().parseAs(Track.class);
		String resolvedClientId = "?client_id=" + CLIENT_ID;
		writeOutInfo(resolvedClientId, track);

		/*
		 * Fields for tracks
		 */
		URL cloudSoundDL = new URL(track.stream_url + resolvedClientId);
		byte[] result = inputStreamToByteArray(cloudSoundDL.openStream());
		String trackTitle = track.title + ".mp3";

		byteArrayToFile(result, trackTitle);
		tagMediaWithMetaData(track, trackTitle);

		System.out.println("Done!");
	}

	private static void writeOutInfo(String resolvedClientId, Track track)
	{
		System.out.println();
		System.out.println("-----------------------------------------------");
		System.out.println("Kind: " + track.kind);
		System.out.println("ID: " + track.id);
		/*
		 * Fields for tracks
		 */
		System.out.println("Title: " + track.title);
		System.out.println("Stream_Url: " + track.stream_url);
		System.out.println("Permalink_Url: " + track.permalink_url);
		System.out.println("Is downloadable: " + track.downloadable);
		if (track.downloadable)
		{
			System.out.println("Download Link: " + track.download_url);
		}
		System.out.println();
		System.out.println("-----------------------------------------------");
		System.out.println("Streamable URL for download: " + track.stream_url + resolvedClientId);
		System.out.println("Attempting Download");
	}

	private static void tagMediaWithMetaData(Track track, String trackTitle)
			throws IOException, UnsupportedTagException, InvalidDataException, NotSupportedException {
		Mp3File mp3file = new Mp3File(trackTitle);
		ID3v1 id3v1Tag;
		if (mp3file.hasId3v1Tag())
		{
			id3v1Tag =  mp3file.getId3v1Tag();
			System.out.println("[id3v1Tag] Title: " + id3v1Tag.getTitle());
		}
		else
		{
			// mp3 does not have an ID3v1 tag, let's create one..
			id3v1Tag = new ID3v1Tag();
			mp3file.setId3v1Tag(id3v1Tag);
			id3v1Tag.setTitle(track.title);
			mp3file.save("tagged/" + trackTitle);
			if (mp3file.hasId3v1Tag())
			{
				ID3v1 id3v1TagTest =  mp3file.getId3v1Tag();
				System.out.println("[id3v1TagTest] Title: " + id3v1TagTest.getTitle());
			}
		}
	}

	public static byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int bytesRead;
		while ((bytesRead = inStream.read(buffer)) > 0) {
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	public static void byteArrayToFile(byte[] byteArray, String outFilePath) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(outFilePath);
		try {
			fos.write(byteArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
