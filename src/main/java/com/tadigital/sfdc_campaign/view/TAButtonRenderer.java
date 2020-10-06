/**
 * 
 */
package com.tadigital.sfdc_campaign.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.data.renderer.BasicRenderer;
import com.vaadin.flow.data.renderer.ClickableRenderer;
import com.vaadin.flow.data.renderer.Rendering;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;

/**
 * @author Ravi.sangubotla
 *
 */
public class TAButtonRenderer<SOURCE> extends BasicRenderer<SOURCE, String> implements ClickableRenderer<SOURCE> {

	private static final long serialVersionUID = -7773248454815486141L;
	private List<ItemClickListener<SOURCE>> listeners = new ArrayList<>(1);

	public TAButtonRenderer(String label) {
		this(value -> label);
	}

	public TAButtonRenderer(String label, ItemClickListener<SOURCE> clickListener) {
		this(label);
		addItemClickListener(clickListener);
	}

	public TAButtonRenderer(ValueProvider<SOURCE, String> labelProvider) {
		super(labelProvider);
	}

	public TAButtonRenderer(ValueProvider<SOURCE, String> labelProvider, ItemClickListener<SOURCE> clickListener) {
		this(labelProvider);
		addItemClickListener(clickListener);
	}

	@Override
	public Registration addItemClickListener(ItemClickListener<SOURCE> listener) {

		listeners.add(listener);
		return () -> listeners.remove(listener);
	}

	@Override
	public List<ItemClickListener<SOURCE>> getItemClickListeners() {
		return Collections.unmodifiableList(listeners);
	}

	@Override
	protected String getTemplateForProperty(String property, Rendering<SOURCE> context) {
		String templatePropertyName = getTemplatePropertyName(context);
		String eventName = templatePropertyName + "_event";
		String disabledName = templatePropertyName + "_disabled";
		setEventHandler(eventName, this::onClick);
		return String.format("<button on-click=\"%s\" disabled=\"[[item.%s]]\">%s</button>", eventName, disabledName,
				property);
	}

	
	@Override
	public Component createComponent(SOURCE item) {
		NativeButton button = new NativeButton(getValueProvider().apply(item));
		button.addClickListener(event -> getItemClickListeners().forEach(listener -> listener.onItemClicked(item)));
		button.addClassName("logout");
		return button;
	}

}
