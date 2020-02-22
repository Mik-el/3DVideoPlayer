/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//MTK_OP01_PROTECT_START
package com.estar.video.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;

/**
 * 视频详细信息对话框
 * 
 * @author zgl
 * 
 */
public class MovieDetailDialog extends AlertDialog implements DialogInterface.OnClickListener {
	private static final int BTN_OK = DialogInterface.BUTTON_POSITIVE;
	private final Context mContext;
	private View mView;
	private TextView mTitleView;
	private TextView mTimeView;
	private TextView mPathView;
	private TextView mDurationView;
	private TextView mFileSizeView;
	private TextView mIs3dView;
	private final ViewHolder mHolder;

	public MovieDetailDialog(final Context context, final ViewHolder holder) {
		super(context);
		mContext = context;
		mHolder = holder;
	}

	public static void showDetail(Context context, VideoObject videoObject) {
		final ViewHolder holder = new ViewHolder();
		holder.mTitle = videoObject.getTitle();
		holder.mDateTaken = videoObject.getDateTaken();
		holder.mDuration = videoObject.getDuration();
		holder.mFileSize = videoObject.getSize();
		holder.mData = videoObject.getPath();
		if (videoObject.is3dVideo()) {
			holder.mSupport3D = true;
		} else {
			holder.mSupport3D = false;
		}
		final MovieDetailDialog detailDialog = new MovieDetailDialog(context, holder);
		detailDialog.setTitle(R.string.media_detail);
		detailDialog.show();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setTitle(R.string.media_detail);
		mView = getLayoutInflater().inflate(R.layout.movie_detail_dialog, null);
		if (mView != null) {
			setView(mView);
			mTitleView = (TextView) mView.findViewById(R.id.title);
			mTimeView = (TextView) mView.findViewById(R.id.time);
			mDurationView = (TextView) mView.findViewById(R.id.duration);
			mPathView = (TextView) mView.findViewById(R.id.path);
			mFileSizeView = (TextView) mView.findViewById(R.id.filesize);
			mIs3dView = (TextView) mView.findViewById(R.id.is3d);

			mTitleView.setText(mContext.getString(R.string.detail_title, mHolder.mTitle));
			mPathView.setText(mContext.getString(R.string.detail_path, mHolder.mData));
			mDurationView.setText(mContext.getString(R.string.detail_duration, Utils.formatDuration((int) mHolder.mDuration)));
			mTimeView.setText(mContext.getString(R.string.detail_date_taken, Utils.dateFormat(mHolder.mDateTaken)));
			mFileSizeView.setText(mContext.getString(R.string.detail_filesize, Formatter.formatFileSize(mContext, mHolder.mFileSize)));
			if (mHolder.mSupport3D) {
				mIs3dView.setText(mContext.getString(R.string.detail_is3d, mContext.getString(R.string.yes)));
			} else {
				mIs3dView.setText(mContext.getString(R.string.detail_is3d, mContext.getString(R.string.not)));
			}
			setButton(BTN_OK, mContext.getString(android.R.string.ok), this);
		} else {
			Utils.showLogError("onCreate() mView is null");
		}
		super.onCreate(savedInstanceState);
	}

	public void onClick(final DialogInterface dialogInterface, final int button) {

	}

	/**
	 * 保存详细信息
	 * @author zgl
	 *
	 */
	public static class ViewHolder {
		long mId;
		String mTitle;
		String mMimetype;
		String mData;
		long mDuration;
		long mDateTaken;
		long mFileSize;
		boolean mIsDrm;
		long mDateModified;
		boolean mSupport3D;
		ImageView mIcon;
		TextView mTitleView;
		TextView mFileSizeView;
		TextView mDurationView;

		@Override
		public String toString() {
			return new StringBuilder().append("ViewHolder(mId=").append(mId).append(", mTitle=").append(mTitle).append(", mDuration=").append(mDuration).append(", mIsDrm=").append(mIsDrm)
					.append(", mData=").append(mData).append(", mFileSize=").append(mFileSize).append(", mSupport3D=").append(mSupport3D).append(")").toString();
		}

		/**
		 * just clone info
		 */
		@Override
		protected ViewHolder clone() {
			final ViewHolder holder = new ViewHolder();
			holder.mId = mId;
			holder.mTitle = mTitle;
			holder.mMimetype = mMimetype;
			holder.mData = mData;
			holder.mDuration = mDuration;
			holder.mDateTaken = mDateTaken;
			holder.mFileSize = mFileSize;
			holder.mIsDrm = mIsDrm;
			holder.mDateModified = mDateModified;
			holder.mSupport3D = mSupport3D;
			return holder;
		}
	}
}