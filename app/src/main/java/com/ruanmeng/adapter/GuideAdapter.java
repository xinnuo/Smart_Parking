/*
  created by 小卷毛, 2018/11/12
  Copyright (c) 2018, 416143467@qq.com All Rights Reserved.
  #                   *********                            #
  #                  ************                          #
  #                  *************                         #
  #                 **  ***********                        #
  #                ***  ****** *****                       #
  #                *** *******   ****                      #
  #               ***  ********** ****                     #
  #              ****  *********** ****                    #
  #            *****   ***********  *****                  #
  #           ******   *** ********   *****                #
  #           *****   ***   ********   ******              #
  #          ******   ***  ***********   ******            #
  #         ******   **** **************  ******           #
  #        *******  ********************* *******          #
  #        *******  ******************************         #
  #       *******  ****** ***************** *******        #
  #       *******  ****** ****** *********   ******        #
  #       *******    **  ******   ******     ******        #
  #       *******        ******    *****     *****         #
  #        ******        *****     *****     ****          #
  #         *****        ****      *****     ***           #
  #          *****       ***        ***      *             #
  #            **       ****        ****                   #
 */
package com.ruanmeng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.ruanmeng.share.BaseHttp;
import com.ruanmeng.smart_parking.R;
import com.sunfusheng.glideimageview.progress.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class GuideAdapter extends StaticPagerAdapter {

    private Context context;
    private List<String> imgs = new ArrayList<>();

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
        notifyDataSetChanged();
    }

    public GuideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_banner_img, null);
        ImageView iv_img = view.findViewById(R.id.iv_banner_img);

        GlideApp.with(context)
                .load(BaseHttp.INSTANCE.getBaseImg() + imgs.get(position))
                .dontAnimate()
                .into(iv_img);
        return view;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }
}
