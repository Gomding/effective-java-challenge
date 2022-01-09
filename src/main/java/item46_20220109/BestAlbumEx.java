package item46_20220109;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.toMap;

public class BestAlbumEx {
    public static void main(String[] args) {
        List<Album> albums = Arrays.asList(
                new Album("찰리 1집", new Artist("찰리"), 10),
                new Album("찰리 2집", new Artist("찰리"), 10000),
                new Album("초콜릿 1집", new Artist("초콜릿"), 200),
                new Album("초콜릿 3집", new Artist("초콜릿"), 300),
                new Album("초콜릿 2집", new Artist("초콜릿"), 1000)
        );
        Map<Artist, Album> bestAlbum = albums.stream().collect(
                toMap(Album::artist, a -> a, maxBy(comparing(Album::sales))));
        for (Map.Entry<Artist, Album> artistAlbumEntry : bestAlbum.entrySet()) {
            System.out.printf("아티스트 : %s, 앨범명 : %s%n", artistAlbumEntry.getKey().name(), artistAlbumEntry.getValue().name());
        }
    }
}

class Album {
    private final String name;
    private final Artist artist;
    private final int sales;

    public Album(String name, Artist artist, int sales) {
        this.name = name;
        this.artist = artist;
        this.sales = sales;
    }

    public String name() {
        return name;
    }

    public Artist artist() {
        return artist;
    }

    public int sales() {
        return sales;
    }
}

class Artist {
    private final String name;

    public Artist(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
