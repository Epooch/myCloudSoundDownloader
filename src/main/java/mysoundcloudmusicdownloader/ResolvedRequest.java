package mysoundcloudmusicdownloader;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

/**
 * Used to resolve urls and return the type of request for retrieval and class
 * type for parsing.
 *
 * @author eric
 *
 */
public class ResolvedRequest {

	static final String RESOLVE_URL = "https://api.soundcloud.com/resolve.json?url=";

	@Key
	public String kind;

	@Key
	public int id;

	@Key
	public String uri;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public static class RequestURL extends GenericUrl {

		public String fields;

		public RequestURL(String requestUrl)
		{
			super(requestUrl);
			this.fields = "kind,id,uri,statuscode";
		}
		// If json resolve stops working decoded urls the following will encode
		// the url for json to resolve
		// URLEncoder.encode("https://soundcloud.com/hytydremixs/cash-cash-millionaire-ft-nelly-hytyd-remix",
		// java.nio.charset.StandardCharsets.UTF_8.toString());

	}
}
