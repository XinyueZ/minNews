package com.gmail.hasszhao.mininews.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.orchestr8.android.api.AlchemyAPI;


public class LoadDetailsContent extends AsyncTask<String, Document, Document> {

	private static final String API_KEY = "2b1a8e7f45b333378006d0b635360406f2b8c621";
	private final WeakReference<TextView> mOutput;


	public LoadDetailsContent(TextView _output) {
		super();
		mOutput = new WeakReference<TextView>(_output);
		_output.setVisibility(View.GONE);
	}


	@Override
	protected Document doInBackground(String... _params) {
		Document doc = null;
		AlchemyAPI api = null;
		String url = _params[0];
		try {
			api = AlchemyAPI.GetInstanceFromString(API_KEY);
			doc = api.URLGetText(url);
		} catch (IOException _e) {
			_e.printStackTrace();
		} catch (SAXException _e) {
			_e.printStackTrace();
		} catch (ParserConfigurationException _e) {
			_e.printStackTrace();
		} catch (IllegalArgumentException _e) {
			_e.printStackTrace();
		}
		return doc;
	}


	@Override
	protected void onPostExecute(Document _result) {
		TextView tv = mOutput.get();
		if (tv != null) {
			try {
				showDocInTextView(tv, _result, false);
				tv.setVisibility(View.VISIBLE);
			} catch (Exception _e) {
				_e.toString();
			}
		}
	}


	private void showDocInTextView(TextView _tvOutput, Document doc, boolean showSentiment) {
		_tvOutput.setText("");
		if (doc == null) {
			return;
		}
		Element root = doc.getDocumentElement();
		NodeList items = root.getElementsByTagName("text");
		if (showSentiment) {
			NodeList sentiments = root.getElementsByTagName("sentiment");
			for (int i = 0; i < items.getLength(); i++) {
				Node concept = items.item(i);
				String astring = concept.getNodeValue();
				astring = concept.getChildNodes().item(0).getNodeValue();
				_tvOutput.append("\n" + astring);
				if (i < sentiments.getLength()) {
					Node sentiment = sentiments.item(i);
					Node aNode = sentiment.getChildNodes().item(1);
					Node bNode = aNode.getChildNodes().item(0);
					_tvOutput.append(" (" + bNode.getNodeValue() + ")");
				}
			}
		} else {
			for (int i = 0; i < items.getLength(); i++) {
				Node concept = items.item(i);
				String astring = concept.getNodeValue();
				astring = concept.getChildNodes().item(0).getNodeValue();
				_tvOutput.append("\n" + astring);
			}
		}
	}
}
