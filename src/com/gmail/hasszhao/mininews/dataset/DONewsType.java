package com.gmail.hasszhao.mininews.dataset;

public final class DONewsType {
    private String  ItemName;
    private String  Query;
    private boolean IsNew;
    private byte[]  Image;
    private byte[]  Tag;

    public DONewsType( String _itemName, String _query, boolean _isNew, byte[] _image, byte[] _tag ) {
        super();
        ItemName = _itemName;
        Query = _query;
        IsNew = _isNew;
        Image = _image;
        Tag = _tag;
    }

    public String getItemName() {
        return ItemName;
    }

    public String getQuery() {
        return Query;
    }

    public boolean isIsNew() {
        return IsNew;
    }

    public byte[] getImage() {
        return Image;
    }

    public byte[] getTag() {
        return Tag;
    }

}
