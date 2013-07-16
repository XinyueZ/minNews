package com.gmail.hasszhao.mininews.fragments.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.fragments.basic.BasicDialogFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class AskOpenDetailsMethodFragment extends BasicDialogFragment implements OnClickListener {

	private static final int LAYOUT = R.layout.fragment_ask_open_details_method;


	public enum OpenContentMethod {
		IN_APP(0), IN_BROWSER(1);

		private final int value;


		private OpenContentMethod(int _value) {
			value = _value;
		}


		public int toInt() {
			return value;
		}


		public static OpenContentMethod fromValue(int _value) {
			switch (_value) {
				case 0:
					return IN_APP;
				case 1:
					return IN_BROWSER;
				default:
					return null;
			}
		}
	}


	private OpenContentMethod method;


	public static AskOpenDetailsMethodFragment newInstance(Fragment _parentFragment) {
		AskOpenDetailsMethodFragment f = (AskOpenDetailsMethodFragment) AskOpenDetailsMethodFragment.instantiate(
				_parentFragment.getActivity().getApplication(), AskOpenDetailsMethodFragment.class.getName());
		f.setTargetFragment(_parentFragment, 0);
		return f;
	}


	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setCancelable(false);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			initCheckbox(_view);
			initRadios(_view);
			initButtons(_view);
		}
	}


	private void initButtons(View _view) {
		_view.findViewById(R.id.btn_open).setOnClickListener(this);
		_view.findViewById(R.id.btn_do_not_open).setOnClickListener(this);
	}


	private void initCheckbox(View _view) {
		CheckBox cbAsk = (CheckBox) _view.findViewById(R.id.ch_do_not_ask_again);
		cbAsk.setOnClickListener(this);
	}


	private void initRadios(View _view) {
		RadioButton rbInApp = (RadioButton) _view.findViewById(R.id.rb_open_in_app);
		rbInApp.setOnClickListener(this);
		RadioButton rbInBrowser = (RadioButton) _view.findViewById(R.id.rb_open_in_browser);
		rbInBrowser.setOnClickListener(this);
		method = OpenContentMethod.fromValue(Prefs.getInstance().getOpenDetailsMethod());
		switch (method) {
			case IN_APP:
				rbInApp.setChecked(true);
				rbInBrowser.setChecked(false);
				break;
			case IN_BROWSER:
				rbInApp.setChecked(false);
				rbInBrowser.setChecked(true);
				break;
		}
	}


	@Override
	public void onClick(View _v) {
		View v = getView();
		if (_v instanceof RadioButton) {
			chooseRadios(v);
			return;
		}
		if (_v instanceof CheckBox) {
			CheckBox cb = (CheckBox) _v;
			Prefs.getInstance().setDontAskForOpeningDetailsMethod(cb.isChecked());
			return;
		}
		switch (_v.getId()) {
			case R.id.btn_open:
				open();
				break;
		}
		close();
	}


	private void chooseRadios(View _v) {
		int selectedId = ((RadioGroup) _v.findViewById(R.id.rg_open_details)).getCheckedRadioButtonId();
		switch (selectedId) {
			case R.id.rb_open_in_app:
				method = OpenContentMethod.IN_APP;
				break;
			case R.id.rb_open_in_browser:
				method = OpenContentMethod.IN_BROWSER;
				break;
		}
	}


	private void open() {
		if (method != null) {
			// defensive done
			Prefs.getInstance().setOpenDetailsMethod(method.toInt());
			Fragment f = getTargetFragment();
			if (f instanceof INewsListItemProvider) {
				INewsListItemProvider p = (INewsListItemProvider) f;
				p.openDetails(method);
			}
		}
	}
}
