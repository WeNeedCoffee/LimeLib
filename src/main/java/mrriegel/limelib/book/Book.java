package mrriegel.limelib.book;

import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Book {
	public static final Map<Impl, Chapter> chapterMap = Maps.newHashMap();

	protected List<Chapter> chapters = Lists.newArrayList();
	public Chapter lastChapter;
	public SubChapter lastSubChapter;
	public int lastPage;

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void init() {
		chapters.clear();
		Chapter blocks = new Chapter("Blocks");
		blocks.subChapters.add(new SubChapter("Dirt", new ItemStack(Blocks.DIRT)).setText("very useless stuff, not to mention, as i do"));
		blocks.subChapters.add(new SubChapter("Sand", new ItemStack(Blocks.SAND)).setText("fallen fallen fallen fallen fallen fallen fallen fallen"));
		blocks.subChapters.add(new SubChapter("furnace", new ItemStack(Blocks.FURNACE)).setText("Der Ofen hat ein eigenes Menü, in dem die Schmelzvorgänge vorgenommen werden können. Das Menü besteht aus 3 Feldern: das obere für den Rohstoff"));
		blocks.subChapters.add(new SubChapter("kaktus", new ItemStack(Blocks.CACTUS)).setText("Der Kaktus kann normalerweise nicht neben anderen Blöcken platziert werden. Ausnahmen sind einige unsolide Blöcke wie Zuckerrohr, Hebel, Knöpfe, Blumen, Setzlinge, Rüstungsständer, Fackeln, Teppiche, Schienen, Schnee, Verstärker und Komparatoren"));
		blocks.subChapters
				.add(new SubChapter("DAB DAB KAP")
						.setText("Nach der Rückkehr vom UEFA-Entwicklungsturnier mit der deutschen U 17-Nationalmannschaft in Kroatien wartet auf die vier Schalker Lukas Ahrend, Andriko Smolinski, Justin Neiß und Okan Yilmaz eine besondere Partie in der Bundesliga. Borussia Dortmund kommt am Sonntag (ab 11 Uhr) nach Gelsenkirchen, dabei wird Ahrend noch einmal die Schalker Kapitänsbinde tragen. Die Nachwuchsabteilung von Hertha BSC freut sich über einen Neubau, der SC Freiburg tankt im Verbandspokal Selbstvertrauen und bei Fortuna Düsseldorf tritt Tim Thomas Brdaric in die Fußstapfen seines prominenten Vaters. Die DFB.de-Splitter aus der B-Junioren-Bundesliga. FC SCHALKE 04: Im Derby gegen den Tabellenzweiten und dreimaligen Staffelsieger Borussia Dortmund am Sonntag (ab 11 Uhr) muss Stephan Schmidt, Trainer von West-Spitzenreiter FC Schalke 04, letztmals auf Abwehrspieler und Kapitän Luca Beckenbauer verzichten. Der 16 Jahre alte Enkel von Weltmeister Franz Beckenbauer war nach seiner Roten Karte beim 1:0-Auswärtssieg in Bochum vom DFB-Sportgericht für drei Meisterschaftsspiele gesperrt worden. Auch ohne den Zugang vom FC Bayern München setzten sich die Knappen gegen den FC Hennef 05 (1:0) und den 1. FC Mönchengladbach (8:0) durch, sind damit vor dem Duell mit dem BVB noch ohne Punktverlust und gehen mit drei Zählern Vorsprung in die Partie. Als Schalker Spielführer wird Luca Beckenbauer von U 17-Nationalspieler Lukas Ahrend vertreten. HAMBURGER SV: Christian Titz, U 17-Trainer beim Hamburger SV muss in den nächsten Monaten ohne Innenverteidiger Jan Sieracki auskommen. Der Zugang von Lech Posen (Polen) musste sich einer Kreuzband-Operation unterziehen. Der 16 Jahre alte Junioren-Nationalspieler seines Heimatlandes muss damit noch mehrere Monate auf sein Debüt in der Staffel Nord/Nordost der höchsten deutschen B-Junioren-Spielklasse warten. HERTHA BSC: Nach fünf Monaten Bauzeit ist die neue Heimat der Nachwuchsabteilung von Hertha BSC fertiggestellt. Alle Mannschaften von der U 15 bis hoch zur U 23 kommen in den Genuss neuer Räumlichkeiten. Erstmals befinden sich alle Umkleideräume an einem Ort. Gläserne Türen und Wände sollen den Austausch unter den Mannschaften fördern. Außerdem wurde auch ein eigener Physiotherapie- und Behandlungsraum mit einer Sauna in Betrieb genommen. Die Vorbereitung auf ihre kommenden Gegner können die Berliner Nachwuchskicker im Kino verfolgen. Auf einem großen Flachbildfernseher können die Trainer ihren Mannschaften taktische Details anschaulich nahebringen."));
		chapters.add(blocks);
		Chapter items = new Chapter("Items");
		items.subChapters.add(new SubChapter("grün", new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage())).setText("Wenn man das Kaktusgrün ins Craftingfeld hineinlegt, lassen sich grüne Wolle, hellgrüner Farbstoff, türkiser Farbstoff, gefärbte Feuerwerkssterne, gefärbte Lederüstungsteile und Gefärbter Ton, Gefärbtes Glas und verschiedene Bannermuster herstellen."));
		items.subChapters.add(new SubChapter("Sterndu&&", new ItemStack(Items.FIREWORK_CHARGE)).setText("Der Feuerwerksstern ist ein Gegenstand, der zum Craften von Feuerwerksraketen benötigt wird und mit dem man Farbe, Form, Fading und weitere Effekte des Feuerwerks bestimmen kann."));
		items.subChapters.add(new SubChapter("dia", new ItemStack(Items.DIAMOND)).setText("Man findet das Diamanterz am häufigsten auf Höhe 5 bis 12. Wenn man das Diamanterz (mit einer Spitzhacke mit Behutsamkeit-Verzauberung) als Block abgebaut hat, kann man den Diamanten auch durch Schmelzen des Erzblocks im Ofen erhalten. Da man aber beim normalen Abbauen den Gegenstand ohne weitere Kosten erhält und eine Glücks-Verzauberung mehr als einen Diamanten erbringt, ist davon abzuraten. Außerdem bringt das Schmelzen weniger Erfahrungspunkte."));
		chapters.add(items);
		Chapter several = new Chapter("Severalkuch");
		several.subChapters.add(new SubChapter("blitz").setText("Ein Gewitter ist eine Wetterlage, die zufällig während Schnee- oder Regenfall auftreten kann. Wie beim Niederschlag (Regen oder Schnee) findet auch das Gewitter immer in der ganzen Welt (bzw. technisch gesehen um jeden Spieler herum) statt, außer natürlich unter der Erde oder in einem niederschlagsfreien Biom (z.B. Wüste)."));
		several.subChapters.add(new SubChapter("cloud").setText("Wolken sind grafische Zusätze, die über den Himmel schweben. Sie dienen nur zur Verschönerung der Umgebung. Insbesondere kommt der Regen nicht aus den Wolken, sondern entsteht immer 10 Blöcke über dem Spieler, auch wenn sich dieser über den Wolken befindet. Man kann mit den Wolken nicht interagieren, aber da sie immer nach Westen ziehen, kann man sie als Orientierungshilfe nutzen.\n\nWolken ziehen als weiße, halb durchsichtige Fläche auf einer Höhe von ca. 128,3 durch die Welt. Wenn die Grafik auf Schön gestellt wurde, bestehen sie aus dreidimensionalen Gebilden, die von dieser Höhe ausgehend 4 Blöcke hoch sind. Dann kann man sich in die Wolken stellen. Im Inneren sind sie allerdings nicht neblig, und auch von innen nach außen sind sie halb durchsichtig. Nur wenn man von unten nach oben durch eine Wolke schauen will, ist sie undurchsichtig, was das Klettern in den Extremen Bergen an der Wolkengrenze gefährlich macht."));
		several.subChapters.add(new SubChapter("fill my pocket").setText("setzt jeden vorhandenen Block mit Anzeige von Zerstörungspartikeln. Durch das Zerstören kann auch ein identischer Block ersetzt werden. Der Block wird dabei abgebaut, d. h. er wird gedroppt."));
		chapters.add(several);
		Chapter dum = new Chapter("dum");
		for (int i = 0; i < 20; i++)
			dum.subChapters.add(new SubChapter("" + new Character((char) (65 + i)) + RandomStringUtils.randomAlphabetic(4)).setText(("" + new Character((char) (65 + i))).toLowerCase()));
		chapters.add(dum);
	}

	public Chapter getChapterForName(String name) {
		for (Chapter c : chapters)
			if (c.name.equalsIgnoreCase(name))
				return c;
		return null;
	}

	public SubChapter getSubChapterForName(String name) {
		for (Chapter c : chapters)
			for (SubChapter sc : c.subChapters)
				if (sc.name.equalsIgnoreCase(name))
					return sc;
		return null;
	}

}
