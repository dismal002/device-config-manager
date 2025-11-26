/*
 * Helper class to build dashboard UI from categories and tiles
 */

package com.deviceconfig.policymanager.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.deviceconfig.policymanager.R;

import java.util.List;

public class DashboardBuilder {
    
    public static void buildDashboard(Context context, ViewGroup container, 
            List<DashboardCategory> categories) {
        if (container == null || categories == null) {
            return;
        }
        
        LayoutInflater inflater = LayoutInflater.from(context);
        Resources res = context.getResources();
        
        container.removeAllViews();
        
        final int count = categories.size();
        
        for (int n = 0; n < count; n++) {
            DashboardCategory category = categories.get(n);
            
            View categoryView = inflater.inflate(R.layout.dashboard_category, container, false);
            
            TextView categoryLabel = (TextView) categoryView.findViewById(R.id.category_title);
            categoryLabel.setText(category.getTitle(res));
            
            DashboardContainerView categoryContent =
                    (DashboardContainerView) categoryView.findViewById(R.id.category_content);
            
            final int tilesCount = category.getTilesCount();
            for (int i = 0; i < tilesCount; i++) {
                DashboardTile tile = category.getTile(i);
                
                DashboardTileView tileView = new DashboardTileView(context);
                updateTileView(context, res, tile, tileView.getImageView(),
                        tileView.getTitleTextView(), tileView.getStatusTextView());
                
                tileView.setTile(tile);
                
                categoryContent.addView(tileView);
            }
            
            // Add the category
            container.addView(categoryView);
        }
    }
    
    private static void updateTileView(Context context, Resources res, DashboardTile tile,
            ImageView tileIcon, TextView tileTextView, TextView statusTextView) {
        
        if (tile.iconRes > 0) {
            tileIcon.setImageResource(tile.iconRes);
        } else {
            tileIcon.setImageDrawable(null);
            tileIcon.setBackground(null);
        }
        
        tileTextView.setText(tile.getTitle(res));
        
        CharSequence summary = tile.getSummary(res);
        if (!TextUtils.isEmpty(summary)) {
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(summary);
        } else {
            statusTextView.setVisibility(View.GONE);
        }
    }
}

