package br.com.smartfingers.android.mwtt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class HistoryRowDialog extends Dialog {
	
	TextView entrada;
	TextView saida;
	TextView almoco;
	TextView total;

	public HistoryRowDialog(Context context) {
		super(context, true, null);
		setContentView(R.layout.history_row_dialog);
		
		entrada = (TextView) findViewById(R.id.horario_entrada);
		saida = (TextView) findViewById(R.id.horario_saida);
		almoco = (TextView) findViewById(R.id.almoco);
		total = (TextView) findViewById(R.id.total);
	}
	
	public void show(TimeTrack tt) {
		setTitle(MyDbAdapter.sdf.format(tt.date));
		entrada.setText(tt.getTimeIn());
		saida.setText(tt.getTimeOut());
		almoco.setText(tt.getTimeLunch());
		total.setText(tt.getTimeTotal());
		super.show();
	}
}
