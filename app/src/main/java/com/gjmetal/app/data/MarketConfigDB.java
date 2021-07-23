package com.gjmetal.app.data;

import android.content.Context;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.star.cache.db.BaseSQLite;

/**
 * Description：行情码表配置
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-10-14 17:39
 */

public class MarketConfigDB extends BaseSQLite {
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "test.db";

	public MarketConfigDB(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void onCreate(BaseSQLite database) {
		database.createTable(Future.class);
	}
	@Override
	public void onUpgrade(BaseSQLite database, int oldVersion, int newVersion) {
	}

}
