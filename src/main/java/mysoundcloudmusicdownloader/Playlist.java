package mysoundcloudmusicdownloader;

import java.util.ArrayList;

import com.google.api.client.util.Key;

/**
 * Used to store a playlist from a json request to soundcloud api.
 * A SoundCloud Set is internally called playlists due to some naming restrictions.
 * @author eric
 *
 */
public class Playlist {

	@Key
	public String kind;

	@Key
	public String title;

	@Key
	public int id;

	@Key
	public ArrayList[] tracks;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList[] getTracks() {
		return tracks;
	}

	public void setTracks(ArrayList[] tracks) {
		this.tracks = tracks;
	}
}
