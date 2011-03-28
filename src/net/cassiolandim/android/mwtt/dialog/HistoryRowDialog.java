package net.cassiolandim.android.mwtt.dialog;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import net.cassiolandim.android.mwtt.entity.TimeTrack;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

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
