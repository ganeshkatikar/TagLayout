/*
 * Copyright 2014 - 2020 Ganesh Katikar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ganidroid.tagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class TagsLayout extends LinearLayout {

    public interface TagClickListener {
        void onClickTag(Tag tag);
    }

    private ArrayList<LinearLayout> mRows = new ArrayList<>();
    private LinearLayout mSingleRow = null;
    private List<Tag> mTags = new ArrayList<>();
    int counterId;
    final int counterIdCompare = 2;

    int parentViewWidth;
    int totalConsumedWidth = 0;
    private TagClickListener listener;
    private boolean mStateMultiSelect = true;
    private View lastSelectedTagView = null;
    private int tagsGravity = 1;
    private int tagLayout = R.layout.item_key_tag;

    private static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.WHITE;

    private int mBackgroundDrawableId = R.drawable.bg_tag;
    private int mTagTextColor = R.drawable.tag_text_color;
    private int mBgColorParent = DEFAULT_CIRCLE_BACKGROUND_COLOR;

    public void setTagClickListener(TagClickListener listener) {
        this.listener = listener;
    }

    public TagsLayout(Context context) {
        super(context);
        this.readStyleParameters(context, null);
        initDefaultLayout();
    }

    public TagsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.readStyleParameters(context, attrs);
        initDefaultLayout();
    }

    public TagsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.readStyleParameters(context, attrs);
        initDefaultLayout();
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.TagsLayout);
        try {
            mStateMultiSelect = a.getBoolean(R.styleable.TagsLayout_tl_bg_color_parent, true);
            tagsGravity = a.getInteger(R.styleable.TagsLayout_tl_gravity, 1);
            mBgColorParent = a.getColor(R.styleable.TagsLayout_tl_bg_color_parent, DEFAULT_CIRCLE_BACKGROUND_COLOR);
        } finally {
            a.recycle();
        }
    }

    private void initDefaultLayout() {
        setOrientation(LinearLayout.VERTICAL);
        removeAllViews();
        TextView tv = new TextView(getContext());
        tv.setText("No Tags Added");
        setGravity(Gravity.CENTER);
        tv.setTextColor(Color.GRAY);
        tv.setGravity(Gravity.CENTER);
        setBackgroundColor(mBgColorParent);
        addView(tv);
    }

    private void initTagView() {
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    parentViewWidth = getWidth();
                    populateTags();
                }
            });
        }
    }

    private void removeTag(Tag tag) {
        if (null != tag && null != mTags && mTags.size() > 0) {
            mTags.remove(tag);
            populateTags();
        }
    }

    private void populateTags() {
        try {
            mRows.clear();
            //noinspection ResourceType
            counterId = 2;
            totalConsumedWidth = 0;
            removeAllViews();
            setOrientation(LinearLayout.VERTICAL);
            if (null != mTags && mTags.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    setLayoutDirection(LAYOUT_DIRECTION_LTR);
                }

                for (Tag tag : mTags) {
                    if (null == mSingleRow) {
                        mSingleRow = getSingleRow();
                    }

                    RelativeLayout mSingleTagLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(tagLayout, null);

                    //RelativeLayout childLayout = mSingleTagLayout.findViewById(R.id.child_tag_layout);
                    RelativeLayout childLayout = mSingleTagLayout.findViewById(R.id.child_tag_layout);
                    childLayout.setBackground(ContextCompat.getDrawable(childLayout.getContext(), getBackgroundDrawable()));
                    //TextView tvTag = mSingleTagLayout.findViewById(R.id.key_text_view);
                    TextView tvTag = mSingleTagLayout.findViewById(R.id.key_text_view);
                    tvTag.setText(tag.getTag());
                    tvTag.setTextColor(ContextCompat.getColorStateList(childLayout.getContext(), getTagTextColor()));
                    mSingleTagLayout.setId(counterId);
                    mSingleTagLayout.measure(0, 0);
                    mSingleTagLayout.setTag(tag);
                    if (isTagMultiSelect()) {
                        mSingleTagLayout.setSelected(tag.isSelected());
                    } else {
                        mSingleTagLayout.setSelected(false);
                    }

                    if (counterIdCompare == counterId) {
                        totalConsumedWidth = mSingleTagLayout.getMeasuredWidth();
                    }

                    if (counterId > 2) {
                        totalConsumedWidth += mSingleTagLayout.getMeasuredWidth();

                        if (totalConsumedWidth < parentViewWidth) {
                            mSingleRow.addView(mSingleTagLayout);
                        } else {
                            mRows.add(mSingleRow);
                            mSingleRow = null;
                            if (null == mSingleRow) {
                                mSingleRow = getSingleRow();
                            }
                            totalConsumedWidth = mSingleTagLayout.getMeasuredWidth();
                            mSingleRow.addView(mSingleTagLayout);
                        }
                    } else {
                        mSingleRow.addView(mSingleTagLayout);
                    }

                    mSingleTagLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View tagLayout) {
                            Tag itsTag = (Tag) tagLayout.getTag();

                            if (null != listener && null != itsTag) {
                                if (tagLayout.isSelected()) {
                                    tagLayout.setSelected(false);
                                } else {
                                    tagLayout.setSelected(true);
                                    listener.onClickTag(itsTag);
                                    if (!isTagMultiSelect()) {
                                        if (null != lastSelectedTagView) {
                                            lastSelectedTagView.setSelected(false);
                                        }
                                        lastSelectedTagView = tagLayout;
                                    }
                                }
                            }
                        }
                    });
                    counterId++;
                }

                mRows.add(mSingleRow);

                for (LinearLayout ll : mRows) {
                    addView(ll);
                }
            }
        } catch (Exception e) {

        }
    }

    public void setTagTextColor(int id) {
        mTagTextColor = id;
        invalidate();
    }

    private int getTagTextColor() {
        return mTagTextColor;
    }

    public void setTagBackgroundDrawable(int id) {
        mBackgroundDrawableId = id;
        invalidate();
    }

    private int getBackgroundDrawable() {
        return mBackgroundDrawableId;
    }

    private boolean isTagMultiSelect() {
        return mStateMultiSelect;
    }

    public void setTagMultiSelect(boolean flag) {
        mStateMultiSelect = flag;
    }

    private LinearLayout getSingleRow() {
        mSingleRow = new LinearLayout(getContext());
        mSingleRow.setOrientation(LinearLayout.HORIZONTAL);
        if (2 == tagsGravity) {
            mSingleRow.setGravity(Gravity.RIGHT);
        } else if (0 == tagsGravity) {
            mSingleRow.setGravity(Gravity.LEFT);
        } else {
            mSingleRow.setGravity(Gravity.CENTER);
        }

        return mSingleRow;
    }

    /**
     * @param mTags List of Tags
     */

    public void setTags(List<Tag> mTags) {
        if (null != this.mTags && null != mTags) {
            this.mTags = mTags;
            initTagView();
        }
    }

    public static class Tag implements Serializable {
        private int id;
        private String tag;
        private boolean selected = false;

        public Tag(int id, String tag) {
            this.id = id;
            this.tag = tag;
        }

        public Tag(int id, String tag, boolean selected) {
            this.id = id;
            this.tag = tag;
            this.selected = selected;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

}
