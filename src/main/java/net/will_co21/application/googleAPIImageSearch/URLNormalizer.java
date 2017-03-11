package net.will_co21.application.googleAPIImageSearch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLNormalizer {
	protected String schemaPart;
	protected String hostPart;
	protected String pathPart;
	protected static final Pattern htmlEntityPattern = Pattern.compile("&.+?;");
	protected static final HashMap<String, String>  decodeEntityMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = -4958869384991862743L;

	{
		put("&amp;", "&");
		put("&lt;", "<");
		put("&gt;", ">");
		put("&quot;", "\"");
		put("&apos;", "'");
		put("&nbsp;", " ");
	}};

	public URLNormalizer(String baseUrl)
	{
		int beforeHostNamePosition = baseUrl.indexOf("://");
		int length = baseUrl.length();

		if(beforeHostNamePosition == -1 || beforeHostNamePosition + 3 == length)
		{
			throw new RuntimeException("url is invalid.");
		}

		this.schemaPart = baseUrl.substring(0, beforeHostNamePosition);

		int pathStartPosition = baseUrl.indexOf('/', beforeHostNamePosition + 3);
		int queryStartPosition = baseUrl.indexOf('?', beforeHostNamePosition + 3);
		int fragmentStartPosition = baseUrl.indexOf('#', beforeHostNamePosition + 3);
		int hostEndPosition = Math.min((pathStartPosition == -1 ? length: pathStartPosition), Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			));

		if(hostEndPosition == length)
		{
			this.hostPart = baseUrl.substring(beforeHostNamePosition + 3, hostEndPosition);
			this.pathPart = "/";
		}
		else
		{
			this.hostPart = baseUrl.substring(beforeHostNamePosition + 3, hostEndPosition);
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);
			if(pathStartPosition == -1 || pathStartPosition > pathEndPosition)
			{
				this.pathPart = "/";
			}
			else
			{
				this.pathPart = normalizedPath(baseUrl.substring(pathStartPosition + 1, pathEndPosition)).orElse("/");
			}
		}
	}

	protected Optional<String> normalizedPath(String path)
	{
		LinkedList<String> pathStack = new LinkedList<String>();

		String[] paths = path.split("/");

		for(String part: paths)
		{
			if(part.equals(".."))
			{
				if(pathStack.size() == 0) return Optional.empty();
				else pathStack.pollLast();
			}
			else if(!part.equals("") && !part.equals("."))
			{
				pathStack.offerLast(part);
			}
		}

		return Optional.of("/" + String.join("/", pathStack.toArray(new String[0])));
	}

	protected String getPath(String url)
	{
		int length = url.length();

		if(url.startsWith("http://") || url.startsWith("https://") ||
				url.startsWith("://") || url.startsWith("//"))
		{
			int beforeHostNamePosition = url.indexOf("//");

			if(beforeHostNamePosition + 2 == length)
			{
				return "";
			}

			int pathStartPosition = url.indexOf('/', beforeHostNamePosition + 2);
			int queryStartPosition = url.indexOf('?', beforeHostNamePosition + 2);
			int fragmentStartPosition = url.indexOf('#', beforeHostNamePosition + 2);
			int pathEndPosition = Math.min(
					(queryStartPosition == -1 ? length : queryStartPosition),
					(fragmentStartPosition == -1 ? length : fragmentStartPosition)
				);

			if(pathStartPosition == -1 || pathStartPosition > pathEndPosition)
			{
				return "";
			}
			else
			{
				return url.substring(pathStartPosition + 1, pathEndPosition);
			}
		}
		else if(url.startsWith("/") || url.startsWith("./"))
		{
			int startPosition = url.indexOf('/') + 1;

			if(startPosition == length) return "";

			int queryStartPosition = url.indexOf('?', startPosition);
			int fragmentStartPosition = url.indexOf('#', startPosition);
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);
			return url.substring(startPosition, pathEndPosition);
		}
		else
		{
			int queryStartPosition = url.indexOf('?');
			int fragmentStartPosition = url.indexOf('#');
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);
			return url.substring(0, pathEndPosition);
		}
	}

	public String normalizedUrl(String url)
	{
		int length = url.length();

		if(url.startsWith("http://") || url.startsWith("https://"))
		{
			int beforeHostNamePosition = url.indexOf("://");

			if(beforeHostNamePosition + 3 == length)
			{
				return "";
			}

			int pathStartPosition = url.indexOf('/', beforeHostNamePosition + 3);
			int queryStartPosition = url.indexOf('?', beforeHostNamePosition + 3);
			int fragmentStartPosition = url.indexOf('#', beforeHostNamePosition + 3);
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);

			if(pathStartPosition == -1 || pathStartPosition > pathEndPosition) return url;
			else
			{
				String path = normalizedPath(getPath(url)).orElse("/");

				if(pathEndPosition == length)
				{
					return url.substring(0, beforeHostNamePosition) + "://" +
							url.substring(beforeHostNamePosition + 3, pathStartPosition) +
							path + (!path.equals("/") && url.charAt(pathEndPosition - 1) == '/' ? "/" : "");
				}
				else
				{
					return url.substring(0, beforeHostNamePosition) + "://" +
							url.substring(beforeHostNamePosition + 3, pathStartPosition) +
							path + (!path.equals("/") && url.charAt(pathEndPosition - 1) == '/' ? "/" : "") +
							url.substring(pathEndPosition, length);
				}
			}
		}
		else if(url.startsWith("://") || url.startsWith("//"))
		{
			int beforeHostNamePosition = url.indexOf("//");

			if(beforeHostNamePosition + 2 == length) return "";

			int pathStartPosition = url.indexOf('/', beforeHostNamePosition + 2);
			int queryStartPosition = url.indexOf('?', beforeHostNamePosition + 2);
			int fragmentStartPosition = url.indexOf('#', beforeHostNamePosition + 2);
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);

			if(pathStartPosition == -1 || pathStartPosition > pathEndPosition)
			{
				return this.schemaPart + "://" + url.substring(beforeHostNamePosition + 2);
			}
			else
			{
				String path = normalizedPath(getPath(url)).orElse("/");

				if(pathEndPosition == length)
				{
					return this.schemaPart + "://" +
							url.substring(beforeHostNamePosition + 2, pathStartPosition) +
							path + (!path.equals("/") && url.charAt(pathEndPosition - 1) == '/' ? "/" : "");
				}
				else
				{
					return this.schemaPart + "://" +
							url.substring(beforeHostNamePosition + 2, pathStartPosition) +
							path + (!path.equals("/") && url.charAt(pathEndPosition - 1) == '/' ? "/" : "") +
							url.substring(pathEndPosition, length);
				}
			}
		}
		else if(url.startsWith("/"))
		{
			int startPosition = 1;

			if(startPosition == length)
			{
				return this.schemaPart + "://" + this.hostPart + this.pathPart;
			}
			int queryStartPosition = url.indexOf('?');
			int fragmentStartPosition = url.indexOf('#');
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);

			String path = normalizedPath(getPath(url)).orElse("/");

			if(pathEndPosition == length)
			{
				return this.schemaPart + "://" + this.hostPart +
						path +
						(!path.equals("/") && url.charAt(pathEndPosition - 1) == '/' ? "/" : "");
			}
			else
			{
				return this.schemaPart + "://" + this.hostPart +
						path +
						(!path.equals("/") && url.charAt(pathEndPosition - 1) == '/' ? "/" : "") +
						url.substring(pathEndPosition, length);
			}
		}
		else
		{
			int queryStartPosition = url.indexOf('?');
			int fragmentStartPosition = url.indexOf('#');
			int pathEndPosition = Math.min(
				(queryStartPosition == -1 ? length : queryStartPosition),
				(fragmentStartPosition == -1 ? length : fragmentStartPosition)
			);

			String path = normalizedPath(getPath(url)).orElse("");

			if(pathEndPosition == length)
			{
				return this.schemaPart + "://" + this.hostPart +
						(this.pathPart.equals("/") ? "" : this.pathPart) +
						path +
						(!path.equals("/") && pathEndPosition > 0 && url.charAt(pathEndPosition - 1) == '/' ? "/" : "");
			}
			else
			{
				return this.schemaPart + "://" + this.hostPart +
						(this.pathPart.equals("/") ? "" : this.pathPart) +
						path +
						(!path.equals("/") && pathEndPosition > 0 && url.charAt(pathEndPosition - 1) == '/' ? "/" : "") +
						url.substring(pathEndPosition, length);
			}
		}
	}

	public static String decodeHtmlEntity(String url)
	{
		StringBuilder sb = new StringBuilder();

		Matcher matcher = htmlEntityPattern.matcher(url);

		int start = 0;

		while(matcher.find())
		{
			if(matcher.start() > 0)
			{
				sb.append(url.substring(start, matcher.start()));
			}

			String entity = matcher.group();

			if(decodeEntityMap.containsKey(entity))
			{
				sb.append(decodeEntityMap.get(entity));
			}
			else
			{
				sb.append(entity);
			}

			start = matcher.end();
		}

		if(start < url.length()) sb.append(url.substring(start, url.length()));

		return sb.toString();
	}
}
