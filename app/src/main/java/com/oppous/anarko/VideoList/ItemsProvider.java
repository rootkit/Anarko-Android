package com.oppous.anarko.VideoList;

public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
