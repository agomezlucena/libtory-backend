package io.github.agomezlucena.libtory.books.domain;

record Title(String title) {
    Title{
        if(title == null || title.isBlank()) throw new InvalidTitle();
    }

    public static Title fromText(String title){
        return new Title(title);
    }
}
