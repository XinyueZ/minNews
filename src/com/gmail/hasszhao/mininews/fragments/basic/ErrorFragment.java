package com.gmail.hasszhao.mininews.fragments.basic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;


public class ErrorFragment extends Fragment implements OnClickListener {

	private final static int LAYOUT = R.layout.fragment_error;
	private final static String KEY_ERROR_TYPE = "error.type";
	public final static String TAG = "TAG.ErrorFragment";


	public interface IErrorResponsible {

		void retry();


		boolean isDataCached();
	}

	public enum ErrorType {
		SERVER_ERROR((byte) 0x1), DATA_LOADING_ERROR((byte) 0x2);

		private final byte type;


		ErrorType(byte _type) {
			type = _type;
		}


		public byte toByte() {
			return type;
		}


		public static ErrorType parseErrorType(byte _type) {
			switch (_type) {
				case 0x1:
					return ErrorType.SERVER_ERROR;
				case 0x2:
					return ErrorType.DATA_LOADING_ERROR;
			}
			return null;
		}
	}


	public static ErrorFragment newInstance(Context _context, ErrorType _errorType) {
		Bundle args = new Bundle();
		args.putByte(KEY_ERROR_TYPE, _errorType.toByte());
		return (ErrorFragment) ErrorFragment.instantiate(_context, ErrorFragment.class.getName(), args);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			errorHandling(_view, ErrorType.parseErrorType(getArguments().getByte(KEY_ERROR_TYPE, (byte) 0x1)));
		}
	}


	private void errorHandling(View _view, ErrorType _type) {
		String topline = getString(R.string.title_error);
		String headline = "";
		boolean showRetry = false;
		switch (_type) {
			case SERVER_ERROR:
				topline = getString(R.string.title_error);
				headline = getString(R.string.msg_error_from_server);
				break;
			case DATA_LOADING_ERROR:
				showRetry = true;
				Fragment f = getTargetFragment();
				if (f instanceof IErrorResponsible) {
					if (((IErrorResponsible) f).isDataCached()) {
						topline = getString(R.string.title_error);
						headline = getString(R.string.msg_error_data_cant_be_loaded_but_read_cached);
					} else {
						topline = getString(R.string.title_error);
						headline = getString(R.string.msg_error_data_cant_be_loaded);
					}
				}
				break;
		}
		((TextView) _view.findViewById(R.id.tv_error_topline)).setText(topline);
		((TextView) _view.findViewById(R.id.tv_error_headline)).setText(headline);
		if (showRetry) {
			_view.findViewById(R.id.btn_retry).setVisibility(View.VISIBLE);
			_view.findViewById(R.id.btn_retry).setOnClickListener(this);
		} else {
			_view.findViewById(R.id.btn_retry).setVisibility(View.GONE);
		}
	}


	@Override
	public void onClick(View _v) {
		Fragment f = getTargetFragment();
		if (f instanceof IErrorResponsible) {
			((IErrorResponsible) f).retry();
		}
	}
}