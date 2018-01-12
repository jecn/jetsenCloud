package com.chanlin.jetsencloud.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chanlin.jetsencloud.R;
import com.chanlin.jetsencloud.entity.ResourceTree;

import java.util.ArrayList;

/**
 * Created by ChanLin on 2018/1/11.
 * jetsenCloud
 * TODO:
 */

public class ResourceAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater layoutInflater;
    ArrayList<ResourceTree> list = new ArrayList<>();
    public ResourceAdapter(Context context, ArrayList<ResourceTree> resourceTreeArrayList){
        mContext = context;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = resourceTreeArrayList;
    }
    public void updateList(ArrayList<ResourceTree> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHodler hodler;
        if (convertView == null){
            hodler = new ViewHodler();
            view = layoutInflater.inflate(R.layout.item_down_file,parent,false);
            hodler.type_icon = (ImageView) view.findViewById(R.id.type_icon);
            hodler.file_title = (TextView) view.findViewById(R.id.file_title);
            hodler.file_title = (TextView) view.findViewById(R.id.file_title);
            view.setTag(hodler);
        }else{
            hodler = (ViewHodler) convertView.getTag();
        }
        ResourceTree tree = list.get(position);
        setIcon(tree.getType(),hodler.type_icon);
        hodler.file_title.setText(tree.getTitle());
        if(tree.getFile_url()  != null && !"".equals(tree.getFile_url())){
            hodler.down.setImageResource(R.mipmap.img_delete);
        }else{
            hodler.down.setImageResource(R.mipmap.img_download);
        }
        return view;
    }
    //type //文件类型(1:word 2:PDF 3:PPT 4:Excel 5:图片 6:视频 7:音频 8:flash
    private void setIcon(int type, ImageView imgView){
        switch (type){
            case 1:
                imgView.setImageResource(R.mipmap.img_type_word);
                break;
            case 2:
                imgView.setImageResource(R.mipmap.img_type_pdf);
                break;
            case 3:
                imgView.setImageResource(R.mipmap.img_type_ppt);
                break;
            case 4:
                imgView.setImageResource(R.mipmap.img_type_excl);
                break;
            case 5:
                imgView.setImageResource(R.mipmap.img_type_img);
                break;
            case 6:
                imgView.setImageResource(R.mipmap.img_type_video);
                break;
            case 7:
                imgView.setImageResource(R.mipmap.img_type_audio);
                break;
            case 8:
                imgView.setImageResource(R.mipmap.img_type_flash);
                break;
            default:
                imgView.setImageResource(R.mipmap.img_type_word);
                break;
        }

    }

    class ViewHodler{
        ImageView type_icon;
        TextView file_title;
        ImageView down;

    }
}
