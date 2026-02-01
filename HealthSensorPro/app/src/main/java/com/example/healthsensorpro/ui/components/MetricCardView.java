package com.example.healthsensorpro.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.example.healthsensorpro.R;

public class MetricCardView extends CardView {
    private TextView titleTextView;
    private TextView valueTextView;
    private TextView unitTextView;

    public MetricCardView(Context context) {
        super(context);
        init(context);
    }

    public MetricCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MetricCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.component_metric_card, this, true);

        titleTextView = findViewById(R.id.title_text);
        valueTextView = findViewById(R.id.value_text);
        unitTextView = findViewById(R.id.unit_text);

        // Set default styling
        setCardElevation(4f);
        setRadius(16f);
        setCardBackgroundColor(Color.parseColor("#E3F2FD"));
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setValue(String value) {
        valueTextView.setText(value);
    }

    public void setUnit(String unit) {
        unitTextView.setText(unit);
    }

    public void setCardColor(int color) {
        setCardBackgroundColor(color);
    }
}