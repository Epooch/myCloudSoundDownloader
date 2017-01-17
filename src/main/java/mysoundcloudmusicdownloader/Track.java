package mysoundcloudmusicdownloader;

import com.google.api.client.util.Key;

/**
 * Used to store a track from a json request to the soundcloud api.
 * A SoundCloud Track.
 *
 * @author eric
 *
 */
public class Track {

	@Key
	public String kind;

	@Key
	public Integer id;

	@Key
	public String created_at;

	@Key
	public Integer user_id;

	@Key
	public String user;

	@Key
	public String title;

	@Key
	public String permalink;

	@Key
	public String permalink_url;

	@Key
	public String uri;

	@Key
	public String sharing;

	@Key
	public String embeddable_by;

	@Key
	public String purchase_url;

	@Key
	public String artwork_url;

	@Key
	public String description;

	@Key
	public String label;

	@Key
	public Long duration;

	@Key
	public String genre;

	@Key
	public String label_id;

	@Key
	public String label_name;

	@Key
	public String release;

//	@Key
//	public String release_day;
//
//	@Key
//	public String release_month;
//
//	@Key
//	public String release_year;

	@Key
	public Integer release_day;

	@Key
	public Integer release_month;

	@Key
	public Integer release_year;

	@Key
	public Boolean streamable;

	@Key
	public Boolean downloadable;

	@Key
	public String state;

	@Key
	public String license;

	@Key
	public String track_type;

	@Key
	public String waveform_url;

	@Key
	public String download_url;

	@Key
	public String stream_url;

	@Key
	public String video_url;

	@Key
	public Integer bpm;

	@Key
	public Boolean commentable;

	@Key
	public String isrc;

	@Key
	public String key_signature;

	@Key
	public int comment_count;

	@Key
	public int download_count;

	@Key
	public int playback_count;

	@Key
	public int favoritings_count;

	@Key
	public String original_format;

	@Key
	public long original_content_size;

	@Key
	public String asset_data;

	@Key
	public String artwork_data;

	@Key
	public String user_favorite;
}