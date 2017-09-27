package mrriegel.testmod;

import org.apache.commons.lang3.RandomStringUtils;

import mrriegel.limelib.book.Article;
import mrriegel.limelib.book.Book;
import mrriegel.limelib.book.Chapter;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class TestBook extends Book {

	boolean init = false;

	public TestBook() {
		if (!init) {
			init();
			init = true;
		}
	}

	public void init() {
		chapters.clear();
		Chapter blocks = new Chapter("Blocks");
		blocks.addArticle(new Article("Dirt", new ItemStack(Blocks.DIRT), new ItemStack(Blocks.GRASS)).setText("VERIE very useless stuff, not to mention, as i do"));
		blocks.addArticle(new Article("Sand", new ItemStack(Blocks.SAND)).setText("fallen fallen fallen fallen fallen fallen fallen fallen"));
		blocks.addArticle(new Article("furnace", new ItemStack(Blocks.FURNACE)).setText("Der Ofen hat ein eigenes Menü, in dem die Schmelzvorgänge vorgenommen werden können. Das Menü besteht aus 3 Feldern: das obere für den Rohstoff \n\n\n   k\na\nf\nf\ne\ne \nn\nn\nn\nn\nn\nn\nn\nn\nn"));
		blocks.addArticle(new Article("kaktuskaktus", new ItemStack(Blocks.CACTUS)).setText("Der <i>Kaktus<r> kann normalerweise nicht neben anderen Blöcken platziert werden. Ausnahmen sind einige unsolide Blöcke wie Zuckerrohr, Hebel, Knöpfe, Blumen, Setzlinge, Rüstungsständer, Fackeln, Teppiche, Schienen, Schnee, Verstärker und Komparatoren"));
		blocks.addArticle(new Article("DAB DAB KAP").setText(
				"Nach der Rückkehr vom <s>UEFA<r>-Entwicklungsturnier mit der deutschen <aqua>U<r> 17-Nationalmannschaft in <20>Kroatien<r> wartet auf die vier Schalker Lukas Ahrend, Andriko Smolinski, Justin Neiß und Okan Yilmaz eine besondere Partie in der Bundesliga. Borussia Dortmund kommt am Sonntag (ab 11 Uhr) nach Gelsenkirchen, dabei wird Ahrend noch einmal die Schalker Kapitänsbinde tragen. Die Nachwuchsabteilung von Hertha BSC freut sich über einen Neubau, der SC Freiburg tankt im Verbandspokal Selbstvertrauen und bei Fortuna Düsseldorf tritt Tim Thomas Brdaric in die Fußstapfen seines prominenten Vaters. Die DFB.de-Splitter aus der B-Junioren-Bundesliga. FC SCHALKE 04: Im Derby gegen den Tabellenzweiten und dreimaligen Staffelsieger Borussia Dortmund am Sonntag (ab 11 Uhr) muss Stephan Schmidt, Trainer von West-Spitzenreiter FC Schalke 04, letztmals auf Abwehrspieler und Kapitän Luca Beckenbauer verzichten. Der 16 Jahre alte Enkel von Weltmeister Franz Beckenbauer war nach seiner Roten Karte beim 1:0-Auswärtssieg in Bochum vom DFB-Sportgericht für drei Meisterschaftsspiele gesperrt worden. Auch ohne den Zugang vom FC Bayern München setzten sich die Knappen gegen den FC Hennef 05 (1:0) und den 1. FC Mönchengladbach (8:0) durch, sind damit vor dem Duell mit dem BVB noch ohne Punktverlust und gehen mit drei Zählern Vorsprung in die Partie. Als Schalker Spielführer wird Luca Beckenbauer von U 17-Nationalspieler Lukas Ahrend vertreten. HAMBURGER SV: Christian Titz, U 17-Trainer beim Hamburger SV muss in den nächsten Monaten ohne Innenverteidiger Jan Sieracki auskommen. Der Zugang von Lech Posen (Polen) musste sich einer Kreuzband-Operation unterziehen. Der 16 Jahre alte Junioren-Nationalspieler seines Heimatlandes muss damit noch mehrere Monate auf sein Debüt in der Staffel Nord/Nordost der höchsten deutschen B-Junioren-Spielklasse warten. HERTHA BSC: Nach fünf Monaten Bauzeit ist die neue Heimat der Nachwuchsabteilung von Hertha BSC fertiggestellt. Alle Mannschaften von der U 15 bis hoch zur U 23 kommen in den Genuss neuer Räumlichkeiten. Erstmals befinden sich alle Umkleideräume an einem Ort. Gläserne Türen und Wände sollen den Austausch unter den Mannschaften fördern. Außerdem wurde auch ein eigener Physiotherapie- und Behandlungsraum mit einer Sauna in Betrieb genommen. Die Vorbereitung auf ihre kommenden Gegner können die Berliner Nachwuchskicker im Kino verfolgen. Auf einem großen Flachbildfernseher können die Trainer ihren Mannschaften taktische Details anschaulich nahebringen."));
		addChapter(blocks);
		Chapter items = new Chapter("Items");
		items.addArticle(new Article("grün", new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage())).setText("Wenn man das Kaktusgrün ins Craftingfeld hineinlegt, lassen sich grüne Wolle, hellgrüner Farbstoff, türkiser Farbstoff, gefärbte Feuerwerkssterne, gefärbte Lederüstungsteile und Gefärbter Ton, Gefärbtes Glas und verschiedene Bannermuster herstellen."));
		items.addArticle(new Article("Sterndu&&", new ItemStack(Items.FIREWORK_CHARGE)).setText("Der Feuerwerksstern ist ein Gegenstand, der zum Craften von Feuerwerksraketen benötigt wird und mit dem man Farbe, Form, Fading und weitere Effekte des Feuerwerks bestimmen kann."));
		items.addArticle(new Article("dia", new ItemStack(Items.DIAMOND)).setText("Man findet das Diamanterz am häufigsten auf Höhe 5 bis 12. Wenn man das Diamanterz (mit einer Spitzhacke mit Behutsamkeit-Verzauberung) als Block abgebaut hat, kann man den Diamanten auch durch Schmelzen des Erzblocks im Ofen erhalten. Da man aber beim normalen Abbauen den Gegenstand ohne weitere Kosten erhält und eine Glücks-Verzauberung mehr als einen Diamanten erbringt, ist davon abzuraten. Außerdem bringt das Schmelzen weniger Erfahrungspunkte."));
		addChapter(items);
		Chapter several = new Chapter("Severalkuchen");
		several.addArticle(new Article("blitz").setText("Ein <u>Gewitter<r> ist eine Wetterlage, die zufällig während Schnee- oder Regenfall auftreten kann. Wie beim Niederschlag (Regen oder Schnee) findet auch das Gewitter immer in der ganzen Welt (bzw. technisch gesehen um jeden Spieler herum) statt, außer natürlich unter der Erde oder in einem niederschlagsfreien Biom (z.B. Wüste)."));
		several.addArticle(new Article("cloud", Blocks.WOOL, Items.DRAGON_BREATH, Items.LEATHER, Blocks.CARPET)
				.setText("Wolken sind grafische Zusätze, die über den Himmel schweben. Sie dienen nur zur Verschönerung der Umgebung. Insbesondere kommt der Regen nicht aus den Wolken, sondern entsteht immer 10 Blöcke über dem Spieler, auch wenn sich dieser über den Wolken befindet. Man kann mit den Wolken nicht interagieren, aber da sie immer nach Westen ziehen, kann man sie als Orientierungshilfe nutzen.\n\nWolken ziehen als weiße, halb durchsichtige Fläche auf einer Höhe von ca. 128,3 durch die Welt. Wenn die Grafik auf Schön gestellt wurde, bestehen sie aus dreidimensionalen Gebilden, die von dieser Höhe ausgehend 4 Blöcke hoch sind. Dann kann man sich in die Wolken stellen. Im Inneren sind sie allerdings nicht neblig, und auch von innen nach außen sind sie halb durchsichtig. Nur wenn man von unten nach oben durch eine Wolke schauen will, ist sie undurchsichtig, was das Klettern in den Extremen Bergen an der Wolkengrenze gefährlich macht."));
		several.addArticle(new Article("fill my pocket").setText("setzt jeden vorhandenen Block mit Anzeige von Zerstörungspartikeln. Durch das Zerstören kann auch ein identischer Block ersetzt werden. Der Block wird dabei abgebaut, d. h. er wird gedroppt."));
		addChapter(several);
		Chapter dum = new Chapter("dum");
		for (int i = 0; i < 10; i++)
			dum.addArticle(new Article("" + new Character((char) (65 + i)) + RandomStringUtils.randomAlphabetic(4)).setText(("" + new Character((char) (65 + i))).toLowerCase()));
		addChapter(dum);
		Chapter item = new Chapter("much");
		item.addArticle(new Article("items", Items.APPLE, Blocks.ANVIL, Items.BAKED_POTATO, Blocks.BEDROCK, Items.CARROT, Blocks.CHEST, Items.DARK_OAK_BOAT, Blocks.DEADBUSH, Items.EGG, Blocks.END_STONE).setText(RandomStringUtils.randomAlphabetic(1700)));
		addChapter(item);
	}

}
