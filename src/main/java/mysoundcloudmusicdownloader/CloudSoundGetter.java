package mysoundcloudmusicdownloader;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;

public class CloudSoundGetter extends GenericUrl {

		public String fields;

		private JsonRequestType jsonRequestType;

		private Track track;

		private Playlist playlist;

		// If json resolve stops working decoded urls the following will encode
		// the url for json to resolve
		// URLEncoder.encode("https://soundcloud.com/hytydremixs/cash-cash-millionaire-ft-nelly-hytyd-remix",
		// java.nio.charset.StandardCharsets.UTF_8.toString());
		public CloudSoundGetter(String requestUrl, String requestType)
		{
			super(requestUrl);
			this.setFields(requestType);
		}

		/**
		 * Passing a requestType will set the fields for json request.
		 * @param requestType
		 */
		private void setFields(String jsonRequestType)
		{
			if(jsonRequestType.equalsIgnoreCase(JsonRequestType.TRACK.toString()))
			{
				this.fields = "kind,id,created_at,user_id,user,title,permalink,permalink_url,uri,sharing,embeddable_by,purchase_url,artwork_url,description,label,duration,genre,label_id,label_name,release,release_day,release_month,release_year,streamable,downloadable,state,license,track_type,waveform_url,download_url,stream_url,video_url,bpm,commentable,isrc,key_signature,original_format,original_content_size,asset_data,artwork_data,user_favorite";
				this.jsonRequestType = JsonRequestType.TRACK;
			}
			else
			{
				System.out.println("You must add the request type to the JsonRequestType enum for processing.");
				System.out.close();
			}
		}

		public void readResponse(HttpResponse response) {
			try {
				System.out.println("Reading request response");
				if(jsonRequestType == JsonRequestType.TRACK)
				{
					track = response.parseAs(Track.class);
					System.out.println("Response Type: " + track.kind);
					System.out.println("Track Id [api]: " + track.id);
				}
				if(jsonRequestType == JsonRequestType.PLAYLIST)
				{
					playlist = response.parseAs(Playlist.class);
					System.out.println("Response Type: " + playlist.kind);
					System.out.println("Playlist Id [api]: " + playlist.id);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * JSON request type; Used with SoundCloudURL.
		 * @author eric
		 *
		 */
		public enum JsonRequestType {
			TRACK,PLAYLIST
		}

		public JsonRequestType getJsonRequestType() {
			return jsonRequestType;
		}

		public void setJsonRequestType(JsonRequestType jsonRequestType) {
			this.jsonRequestType = jsonRequestType;
		}

		public Track getTrack() {
			return track;
		}

		public void setTrack(Track track) {
			this.track = track;
		}

		public Playlist getPlaylist() {
			return playlist;
		}

		public void setPlaylist(Playlist playlist) {
			this.playlist = playlist;
		}

		public boolean isTrack() {
			if(track != null)
				return true;
			return false;
		}

		public boolean isPlaylist() {
			if(playlist != null)
				return true;
			return false;
		}
}
