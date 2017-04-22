package net.will_co21.application.googleAPIImageSearch;

import java.util.ArrayList;

public class IgNoreHostNames {
	protected static ArrayList<String> hosts;

	static {
		hosts = new ArrayList<String>() {{
			add("google.com");
			add("google.is");
			add("google.ie");
			add("google.az");
			add("google.com.af");
			add("google.co.vi");
			add("google.as");
			add("google.ae");
			add("google.dz");
			add("google.com.ar");
			add("google.am");
			add("google.com.ai");
			add("google.com.ag");
			add("google.ad");
			add("google.co.uk");
			add("google.vg");
			add("google.co.il");
			add("google.it");
			add("google.co.in");
			add("google.co.id");
			add("google.co.ug");
			add("google.com.ua");
			add("google.co.uz");
			add("google.com.uy");
			add("google.com.ec");
			add("google.com.eg");
			add("google.ee");
			add("google.com.et");
			add("google.com.sv");
			add("google.com.au");
			add("google.at");
			add("google.com.om");
			add("google.nl");
			add("google.com.gh");
			add("google.gg");
			add("google.gy");
			add("google.kz");
			add("google.com.qa");
			add("google.ca");
			add("google.gm");
			add("google.com.kh");
			add("google.com.cu");
			add("google.gr");
			add("google.ki");
			add("google.kg");
			add("google.com.gt");
			add("google.gp");
			add("google.com.kw");
			add("google.co.ck");
			add("google.gl");
			add("google.ge");
			add("google.hr");
			add("google.co.ke");
			add("google.ci");
			add("google.co.cr");
			add("google.cg");
			add("google.cd");
			add("google.com.sa");
			add("google.ws");
			add("google.st");
			add("google.co.zm");
			add("google.sm");
			add("google.com.sl");
			add("google.dj");
			add("google.com.gi");
			add("google.je");
			add("google.com.jm");
			add("google.com.sg");
			add("google.co.zw");
			add("google.ch");
			add("google.se");
			add("google.es");
			add("google.lk");
			add("google.sk");
			add("google.si");
			add("google.sc");
			add("google.sn");
			add("google.rs");
			add("google.com.vc");
			add("google.sh");
			add("google.co.th");
			add("google.co.kr");
			add("google.com.tw");
			add("google.com.tj");
			add("google.co.tz");
			add("google.cz");
			add("google.cf");
			add("google.com.hk");
			add("google.cl");
			add("google.dk");
			add("google.de");
			add("google.tk");
			add("google.com.do");
			add("google.dm");
			add("google.tt");
			add("google.tm");
			add("google.com.tr");
			add("google.to");
			add("google.com.ng");
			add("google.nr");
			add("google.com.na");
			add("google.nu");
			add("google.com.ni");
			add("google.co.jp");
			add("google.co.nz");
			add("google.com.np");
			add("google.no");
			add("google.com.bh");
			add("google.ht");
			add("google.com.pk");
			add("google.com.pa");
			add("google.vu");
			add("google.bs");
			add("google.com.py");
			add("google.hu");
			add("google.com.bd");
			add("google.tl");
			add("google.pn");
			add("google.com.fj");
			add("google.com.ph");
			add("google.fi");
			add("google.com.pr");
			add("google.com.br");
			add("google.fr");
			add("google.bg");
			add("google.com.bn");
			add("google.bi");
			add("google.com.vn");
			add("google.bj");
			add("google.co.ve");
			add("google.by");
			add("google.com.bz");
			add("google.com.pe");
			add("google.be");
			add("google.ba");
			add("google.co.bw");
			add("google.pt");
			add("google.com.hk");
			add("google.hn");
			add("google.mg");
			add("google.mw");
			add("google.com.mt");
			add("google.com.my");
			add("google.im");
			add("google.fm");
			add("google.co.za");
			add("google.com.mx");
			add("google.mu");
			add("google.co.mz");
			add("google.mv");
			add("google.co.ma");
			add("google.me");
			add("google.ms");
			add("google.jo");
			add("google.la");
			add("google.lv");
			add("google.lt");
			add("google.com.ly");
			add("google.li");
			add("google.ro");
			add("google.lu");
			add("google.rw");
			add("google.co.ls");
			add("google.com.lb");
			add("google.ru");
		}};
	}

	public static boolean contains(String host)
	{
		int hostLen = host.length();

		for(String ignore: hosts)
		{
			int index = host.indexOf(ignore);

			if(index == -1) continue;
			else if(index + ignore.length() == hostLen) return true;
		}

		return false;
	}

	public static void add(String host)
	{
		hosts.add(host);
	}
}
