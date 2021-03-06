package de.azapps.widgets;

import de.azapps.mirakel.settings.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class DueDialog extends AlertDialog {
	private Context		ctx;
	protected VALUE		dayYear	= VALUE.DAY;
	protected int			count;
	private View		dialogView;
	protected String[]	s;

	public enum VALUE {
		MINUTE, HOUR, DAY, MONTH, YEAR;

		public int getInt() {
			switch (this) {
				case DAY:
					return 0;
				case MONTH:
					return 1;
				case YEAR:
					return 2;
				case MINUTE:
					return 3;
				case HOUR:
					return 4;
			default:
				break;
			}
			return 0;
		}
	}

	public void setNegativeButton(int textId, OnClickListener onCancel) {
		setButton(BUTTON_NEGATIVE, this.ctx.getString(textId), onCancel);

	}

	public void setPositiveButton(int textId, OnClickListener onCancel) {
		setButton(BUTTON_POSITIVE, this.ctx.getString(textId), onCancel);

	}

	public void setNeutralButton(int textId, OnClickListener onCancel) {
		setButton(BUTTON_NEUTRAL, this.ctx.getString(textId), onCancel);

	}

	@SuppressLint("NewApi")
	public DueDialog(Context context, final boolean minuteHour) {
		super(context);
		this.ctx = context;
		this.s = new String[100];
		for (int i = 0; i < this.s.length; i++) {
			this.s[i] = (i > 10 ? "+" : "") + (i - 10) + "";
		}

		this.dialogView = getNumericPicker();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			final NumberPicker pickerDay = ((NumberPicker) this.dialogView
					.findViewById(R.id.due_day_year));
			NumberPicker pickerValue = ((NumberPicker) this.dialogView
					.findViewById(R.id.due_val));
			String dayYearValues[] = getDayYearValues(0, minuteHour);

			pickerDay.setDisplayedValues(dayYearValues);
			pickerDay.setMaxValue(dayYearValues.length - 1);
			pickerValue.setMaxValue(this.s.length - 1);
			pickerValue.setValue(10);
			pickerValue.setMinValue(0);
			pickerValue.setDisplayedValues(this.s);
			pickerValue
					.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			pickerDay
					.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			pickerValue.setWrapSelectorWheel(false);
			pickerValue
					.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

						@Override
						public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							pickerDay.setDisplayedValues(getDayYearValues(
									newVal - 10, minuteHour));
							DueDialog.this.count = newVal - 10;
						}
					});
			pickerDay
					.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

						@Override
						public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							switch (newVal) {
								case 0:
									DueDialog.this.dayYear = VALUE.DAY;
									break;
								case 1:
									DueDialog.this.dayYear = VALUE.MONTH;
									break;
								case 2:
									DueDialog.this.dayYear = VALUE.YEAR;
									break;
							default:
								break;
							}

						}
					});

		} else {
			final TextView pickerValue = ((TextView) this.dialogView
					.findViewById(R.id.dialog_due_pick_val));
			pickerValue.setText(this.s[10]);
			this.count = 0;
			final TextView pickerDay = ((TextView) this.dialogView
					.findViewById(R.id.dialog_due_pick_val_day));
			pickerDay.setText(this.ctx.getResources().getQuantityString(
					R.plurals.due_day, 0));
			this.dayYear = VALUE.DAY;

			((Button) this.dialogView.findViewById(R.id.dialog_due_pick_plus_val))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							int val = Integer.parseInt(pickerValue.getText()
									.toString().replace("+", "")) + 10;
							if (val + 1 < DueDialog.this.s.length) {
								pickerValue.setText(DueDialog.this.s[val + 1]);
								DueDialog.this.count = val - 10;
							}
							pickerDay.setText(updateDayYear());

						}
					});
			((Button) this.dialogView.findViewById(R.id.dialog_due_pick_minus_val))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							int val = Integer.parseInt(pickerValue.getText()
									.toString().replace("+", "")) + 10;
							if (val - 1 > 0) {
								pickerValue.setText(DueDialog.this.s[val - 1]);
								DueDialog.this.count = val - 10;
							}
							pickerDay.setText(updateDayYear());
						}
					});
			((Button) this.dialogView.findViewById(R.id.dialog_due_pick_plus_day))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (DueDialog.this.dayYear == VALUE.DAY) {
								DueDialog.this.dayYear = VALUE.MONTH;
							} else if (DueDialog.this.dayYear == VALUE.MONTH) {
								DueDialog.this.dayYear = VALUE.YEAR;
							}
							pickerDay.setText(updateDayYear());
						}
					});
			((Button) this.dialogView.findViewById(R.id.dialog_due_pick_minus_day))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (DueDialog.this.dayYear == VALUE.MONTH) {
								DueDialog.this.dayYear = VALUE.DAY;
							} else if (DueDialog.this.dayYear == VALUE.YEAR) {
								DueDialog.this.dayYear = VALUE.MONTH;
							}
							pickerDay.setText(updateDayYear());
						}
					});
		}
		setView(this.dialogView);

	}

	protected String updateDayYear() {
		switch (this.dayYear) {
			case MINUTE:
				return this.ctx.getResources().getQuantityString(
						R.plurals.due_minute, this.count);
			case HOUR:
				return this.ctx.getResources().getQuantityString(R.plurals.due_hour,
						this.count);
			case DAY:
				return this.ctx.getResources().getQuantityString(R.plurals.due_day,
						this.count);
			case MONTH:
				return this.ctx.getResources().getQuantityString(
						R.plurals.due_month, this.count);
			case YEAR:
				return this.ctx.getResources().getQuantityString(R.plurals.due_year,
						this.count);
		default:
			break;
		}
		return "";

	}

	protected String[] getDayYearValues(int newVal, boolean minutesHour) {
		int size = minutesHour ? 5 : 3;
		int i = 0;
		String[] ret = new String[size];
		if (minutesHour) {
			ret[i++] = this.ctx.getResources().getQuantityString(
					R.plurals.due_minute, newVal);
			ret[i++] = this.ctx.getResources().getQuantityString(R.plurals.due_hour,
					newVal);
		}
		ret[i++] = this.ctx.getResources().getQuantityString(R.plurals.due_day,
				newVal);
		ret[i++] = this.ctx.getResources().getQuantityString(R.plurals.due_month,
				newVal);
		ret[i] = this.ctx.getResources().getQuantityString(R.plurals.due_year,
				newVal);

		return ret;
	}

	protected View getNumericPicker() {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) return getLayoutInflater()
				.inflate(R.layout.due_dialog, null);
		return getLayoutInflater().inflate(R.layout.due_dialog_v10, null);
	}

	@SuppressLint("NewApi")
	public void setValue(int val, VALUE day) {
		if (VERSION.SDK_INT > VERSION_CODES.HONEYCOMB) {
			int _day = this.dayYear.getInt();
			((NumberPicker) this.dialogView.findViewById(R.id.due_day_year))
					.setValue(_day);
			((NumberPicker) this.dialogView.findViewById(R.id.due_val))
					.setValue(val + 10);
		} else {
			this.count = val;
			this.dayYear = day;
			((TextView) this.dialogView.findViewById(R.id.dialog_due_pick_val))
					.setText("" + (val + 10));
			((TextView) this.dialogView.findViewById(R.id.dialog_due_pick_val_day))
					.setText(updateDayYear());
		}

	}

	public int getValue() {
		return this.count;
	}

	public VALUE getDayYear() {
		return this.dayYear;
	}

}
