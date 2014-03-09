package cz.martinbayer.e4.analyser.canvas;

import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.CanvasConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public interface ICanvas {

	/**
	 * Event can be null if selected = false
	 * 
	 * @param event
	 * @param selected
	 */
	CanvasProcessorItem getSelectedProcessorItem();

	ConnectionItem getSelectedConnection();

	/**
	 * When whichever item is hovered on canvas, this method is called. Hovered
	 * item can be of type {@link CanvasConnectionItem} or
	 * {@link CanvasProcessorItem} and is wrapped in source with original event
	 * 
	 * @param event
	 */
	void canvasItemHovered(CanvasEvent<ICanvasItem> event);

	/**
	 * This action is done when mouse is pressed down on processor item which is
	 * placed on canvas and is wrapped in event
	 * 
	 * @param processorItem
	 */
	void canvasProcessorTaken(CanvasEvent<IProcessorItem> processorItem);

	/**
	 * Only one processor can be selected at one time
	 * 
	 * @param processorItem
	 */
	void canvasProcessorSelected(CanvasEvent<IProcessorItem> processorItem);

	/**
	 * This action is done when mouse is pressed down on connection which is
	 * placed on canvas. If mouse is released or moved out from the connection,
	 * the item is untaken.
	 * 
	 * @param connectionItem
	 */
	void canvasConnectionTaken(CanvasEvent<ILine> connectionItem);

	/**
	 * This action is done when mouse is pressed down on connection which is
	 * placed on canvas
	 * 
	 * @param connectionItem
	 */
	void canvasConnectionSelected(CanvasEvent<ILine> connectionItem);

	/**
	 * this method should be called if any of the items on canvas is disposed.
	 * It is used to delete all istances of disposed items
	 * 
	 * @param canvasItem
	 */
	void canvasItemDisposed(CanvasEvent<ICanvasItem> canvasItem);
}
