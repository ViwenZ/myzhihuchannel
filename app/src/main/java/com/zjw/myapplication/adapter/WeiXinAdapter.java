package com.zjw.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zjw.myapplication.R;
import com.zjw.myapplication.entity.WeiXin;

import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */

public class WeiXinAdapter extends RecyclerView.Adapter<WXViewHolder> {
    private Context context;
    private List<WeiXin> weiXins;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public WeiXinAdapter(Context context, List<WeiXin> weiXins) {
        this.context = context;
        this.weiXins = weiXins;
    }

    @Override
    public WXViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_weixin, null, true);
        WXViewHolder holder = new WXViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final WXViewHolder holder, final int position) {
        WeiXin weiXin = weiXins.get(position);
        holder.txtFirst.setText(weiXin.getTitle());
        Glide.with(context)
                .load(weiXin.getFirstImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivFirst);
        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return weiXins.size();
    }


}

class WXViewHolder extends RecyclerView.ViewHolder {
    ImageView ivFirst;
    TextView txtFirst;

    public WXViewHolder(View itemView) {
        super(itemView);
        ivFirst = (ImageView) itemView.findViewById(R.id.iv_item_image);
        txtFirst = (TextView) itemView.findViewById(R.id.txt_item_content);
    }
}