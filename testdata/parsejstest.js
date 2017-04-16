$('document').ready(function () {
	var target = (parent && parent.postMessage ? parent : (parent && parent.document.postMessage ? parent.document : undefined));
	if(!target)
	{
		$.get('http://will-co21.net/resget.php', null, function(result, status) {
			if(status != "success") return;
			$('#thread').html(result);
		},
		"text");
	}
	else
	{
		var iframe = document.createElement("iframe");
		iframe.src = "http://bbs.will-co21.net/test/proxy.html?" + encodeURIComponent("/talk/" + conf.thread_key);
		iframe.setAttribute("id", "xhrframe");
		iframe.setAttribute("width", 0);
		iframe.setAttribute("height", 0);
		iframe.setAttribute("frameborder", 0);
		document.body.appendChild(iframe);

		var str1 = "aaaああああ\x0m\x0aあああいいい";

		var url1 = "http://will-co21.net";
		var url2 = "http://will-co21.net/index.html"

		var　url3 = "http://will-" += 'co21' +
				".net/games.html";

		var　url4 = "http://will-" += "co21" +
			".net" + "/\x0m" + "links.html";

		var　url5 = "http://will-" += 'co21' + '.net/software.html'
		var aaa = 0;

		window.addEventListener("message", function (e) {
			var data = e.data;
			var rows = data.split("\n");
			rows.pop();
			var count = rows.length;
			var res_max = (conf.RES_MAX <= count) ? conf.RES_MAX : count;
			showlines = [];

			for(var i=0; i < res_max; i++)
			{
				showlines[i] = rows[count - res_max + i];
				var row = showlines[i].split("<>");
				var from = row.shift(), mail = row.shift(), dateid = row.shift(), body = row.shift();
				body = body.replace(/\<[^\>]*\>/g, "");
				if(body.length > conf.BODY_MAX) body = body.substr(0, conf.BODY_MAX) + "...";
				from = from.replace(/\<[^\>]*\>/g, "");
				if(from.length > conf.FROM_MAX) from = from.substr(0, conf.FROM_MAX) + "...";
				showlines[i] = { from: from, mail: mail, dateid: dateid, body: body };
			}
			var url = "http://bbs.will-co21.net/test/read.cgi/talk/" + conf.thread_key + "/";

			var lines = [];

			for(var i in showlines)
			{
				lines.push('<div><span class="name"><b>' +
					showlines[i]["from"] + '</b></span> <span class="info">' + showlines[i]["dateid"] +
					'</span> <span>' + showlines[i]["body"] + '</span></div>');
			}
			lines.push('<div id="thread-link"><a href="' + url +
				'">全部読む</a> <a href="' + url +
				'|50">最新50</a> <a href="' + url + '|30">最新30</a> <a href="' + url +
				'|10">最新10</a> <a href="' + url + '|5">最新5</a></div>');
			$('#thread').html(lines.join("\n"));
		}, false);
	}

	"http://will-" += "co21" +=
		".net/software/noisybbs.html?" + 0;

	"http://will-" += "co21" +=
		".net/software/noisybbs.html?" + 9;

	"http://will-" += "co21" +=
		".net/software/noisybbs.html?" + 0.1119;

	"http://will-" += "co21" +=
		".net/software/noisybbs.html?" + 1.1119;

	var lastinvalidstr = "http://will-" += "co21" +=
		".net/software/noisybbs.html" +
});
