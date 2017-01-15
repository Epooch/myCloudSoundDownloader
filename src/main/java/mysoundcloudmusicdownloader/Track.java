package mysoundcloudmusicdownloader;

import com.google.api.client.util.Key;

/**
 * Used to store a track from a json request to the soundcloud api.
 * A SoundCloud Track.
 * @author eric
 *
 */
public class Track {

	@Key
	public String kind;

	@Key
	public int id;

	@Key
	public String created_at;

	@Key
	public int user_id;

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
	public long duration;

	@Key
	public String genre;

	@Key
	public String label_id;

	@Key
	public String label_name;

	@Key
	public String release;

	@Key
	public long release_day;

	@Key
	public long release_month;

	@Key
	public long release_year;

	@Key
	public boolean streamable;

	@Key
	public boolean downloadable;

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
	public String bpm;

	@Key
	public boolean commentable;

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

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getPermalink_url() {
		return permalink_url;
	}

	public void setPermalink_url(String permalink_url) {
		this.permalink_url = permalink_url;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public String getEmbeddable_by() {
		return embeddable_by;
	}

	public void setEmbeddable_by(String embeddable_by) {
		this.embeddable_by = embeddable_by;
	}

	public String getPurchase_url() {
		return purchase_url;
	}

	public void setPurchase_url(String purchase_url) {
		this.purchase_url = purchase_url;
	}

	public String getArtwork_url() {
		return artwork_url;
	}

	public void setArtwork_url(String artwork_url) {
		this.artwork_url = artwork_url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getLabel_id() {
		return label_id;
	}

	public void setLabel_id(String label_id) {
		this.label_id = label_id;
	}

	public String getLabel_name() {
		return label_name;
	}

	public void setLabel_name(String label_name) {
		this.label_name = label_name;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public long getRelease_day() {
		return release_day;
	}

	public void setRelease_day(long release_day) {
		this.release_day = release_day;
	}

	public long getRelease_month() {
		return release_month;
	}

	public void setRelease_month(long release_month) {
		this.release_month = release_month;
	}

	public long getRelease_year() {
		return release_year;
	}

	public void setRelease_year(long release_year) {
		this.release_year = release_year;
	}

	public boolean getStreamable() {
		return streamable;
	}

	public void setStreamable(boolean streamable) {
		this.streamable = streamable;
	}

	public boolean getDownloadable() {
		return downloadable;
	}

	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getTrack_type() {
		return track_type;
	}

	public void setTrack_type(String track_type) {
		this.track_type = track_type;
	}

	public String getWaveform_url() {
		return waveform_url;
	}

	public void setWaveform_url(String waveform_url) {
		this.waveform_url = waveform_url;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getStream_url() {
		return stream_url;
	}

	public void setStream_url(String stream_url) {
		this.stream_url = stream_url;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public String getBpm() {
		return bpm;
	}

	public void setBpm(String bpm) {
		this.bpm = bpm;
	}

	public boolean getCommentable() {
		return commentable;
	}

	public void setCommentable(boolean commentable) {
		this.commentable = commentable;
	}

	public String getIsrc() {
		return isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}

	public String getKey_signature() {
		return key_signature;
	}

	public void setKey_signature(String key_signature) {
		this.key_signature = key_signature;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public int getDownload_count() {
		return download_count;
	}

	public void setDownload_count(int download_count) {
		this.download_count = download_count;
	}

	public int getPlayback_count() {
		return playback_count;
	}

	public void setPlayback_count(int playback_count) {
		this.playback_count = playback_count;
	}

	public int getFavoritings_count() {
		return favoritings_count;
	}

	public void setFavoritings_count(int favoritings_count) {
		this.favoritings_count = favoritings_count;
	}

	public String getOriginal_format() {
		return original_format;
	}

	public void setOriginal_format(String original_format) {
		this.original_format = original_format;
	}

	public long getOriginal_content_size() {
		return original_content_size;
	}

	public void setOriginal_content_size(long original_content_size) {
		this.original_content_size = original_content_size;
	}

	public String getAsset_data() {
		return asset_data;
	}

	public void setAsset_data(String asset_data) {
		this.asset_data = asset_data;
	}

	public String getArtwork_data() {
		return artwork_data;
	}

	public void setArtwork_data(String artwork_data) {
		this.artwork_data = artwork_data;
	}

	public String getUser_favorite() {
		return user_favorite;
	}

	public void setUser_favorite(String user_favorite) {
		this.user_favorite = user_favorite;
	}
}