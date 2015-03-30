/* ---------------------------------------------------------------------------------------------
 *                       Copyright (c) 2013 Capital Alliance Software(Pekall) 
 *                                    All Rights Reserved
 *    NOTICE: All information contained herein is, and remains the property of Pekall and
 *      its suppliers,if any. The intellectual and technical concepts contained herein are
 *      proprietary to Pekall and its suppliers and may be covered by P.R.C, U.S. and Foreign
 *      Patents, patents in process, and are protected by trade secret or copyright law.
 *      Dissemination of this information or reproduction of this material is strictly 
 *      forbidden unless prior written permission is obtained from Pekall.
 *                                     www.pekall.com
 *--------------------------------------------------------------------------------------------- 
*/

package com.common.utils;

import android.content.Context;

	public class ScreenUtil
	{
	  public static int dip2px(Context paramContext, float paramFloat)
	  {
	    return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
	  }

	  public static int px2dip(Context paramContext, float paramFloat)
	  {
	    return (int)(0.5F + paramFloat / paramContext.getResources().getDisplayMetrics().density);
	  }

	  public static int px2sp(Context paramContext, float paramFloat)
	  {
	    return (int)(0.5F + paramFloat / paramContext.getResources().getDisplayMetrics().scaledDensity);
	  }

	  public static int sp2px(Context paramContext, float paramFloat)
	  {
	    return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().scaledDensity);
	  }
	}
