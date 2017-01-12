package mysoundcloudmusicdownloader;

import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;


public class MySoundCloudDownloader {

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static class MusicFeed {
		@Key
		public List<Song> list;

		@Key("has_more")
		public boolean hasMore;
	}

	public static class Song {
		@Key
		public String id;

		@Key
		public List<String> tags;

		@Key
		public String title;

		@Key
		public String url;
	}

	public static class SoundCloudURL extends GenericUrl {
		public SoundCloudURL(String encodeUrl)
		{
			super(encodeUrl);
		}

		public String fields;
	}

	private static void run() throws Exception
	{
		HttpRequestFactory requestFactory =
				HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});
		SoundCloudURL url = new SoundCloudURL("https://soundcloud.com/gladiator/don-diablo-m1-stinger");
		url.fields = "id,tags,title,url";
		HttpRequest request = requestFactory.buildGetRequest(url);
		MusicFeed musicFeed = request.execute().parseAs(MusicFeed.class);
		if(musicFeed.list.isEmpty())
		{
			System.out.println("No Songs found.");
		}
		else
		{
			if(musicFeed.hasMore) {
				System.out.print("First ");
			}
			System.out.println(musicFeed.list.size() + " songs found: ");
			for(Song song : musicFeed.list)
			{
				System.out.println();
				System.out.println("~~~~");
				System.out.println("ID: " + song.id);
				System.out.println("Title: " + song.title);
				System.out.println("URL: " + song.url);
			}
		}
	}

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
}