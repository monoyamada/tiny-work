package study.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class Hello {
	protected static final int EXIT_SUCCESS = 1;
	protected static final Entry[] EMPTY_ENTRY_ARRAY = {};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		WindowListener listener = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Hello.exit(Hello.EXIT_SUCCESS);
			}
		};
		frame.addWindowListener(listener);
		frame.setContentPane(Hello.newContentPane());
		frame.setJMenuBar(Hello.newMenuBar());
		frame.pack();
		frame.setVisible(true);
	}
	protected static JMenuBar newMenuBar() {
		final JMenuBar menuBar = new JMenuBar();
		final JMenu fileMenu = new JMenu("File");
		{
			fileMenu.addSeparator();
			final Action action = new AbstractAction("Exit") {
				public void actionPerformed(ActionEvent e) {
					Hello.exit(Hello.EXIT_SUCCESS);
				}
			};
			fileMenu.add(action);
		}
		menuBar.add(fileMenu);
		return menuBar;
	}
	protected static JComponent newContentPane() {
		final JPanel panel = new JPanel(new BorderLayout());
		final TableModel model = new AbstractTableModel() {
			private Map.Entry[] array;

			@SuppressWarnings("unused")
			protected Map.Entry[] getArray() {
				if (this.array == null) {
					this.array = this.newArray();
				}
				return this.array;
			}
			@SuppressWarnings("unchecked")
			protected Entry[] newArray() {
				final Entry[] array = System.getProperties().entrySet().toArray(
						EMPTY_ENTRY_ARRAY);
				final Comparator<Entry> fnc = new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						if (o1 == o2) {
							return 0;
						} else if (o1 == null) {
							return 1;
						} else if (o2 == null) {
							return -1;
						}
						try {
							final String s1 = (String) o1.getKey();
							final String s2 = (String) o2.getKey();
							if (s1.length() < s2.length() && false) {
								return -1;
							} else if (s2.length() < s1.length() && false) {
								return 1;
							}
							return s1.compareTo(s2);
						} catch (Exception ex) {
							return 0;
						}
					}
				};
				Arrays.sort(array, fnc);
				return array;
			}
			@SuppressWarnings("unused")
			protected void setArray(Map.Entry[] array) {
				this.array = array;
			}
			public int getRowCount() {
				return this.getArray().length;
			}
			public int getColumnCount() {
				return 2;
			}
			public Object getValueAt(int row, int col) {
				final Map.Entry[] array = this.getArray();
				if (0 <= row && row < array.length) {
					switch (col) {
					case 0:
						return array[row].getKey();
					case 1:
						return array[row].getValue();
					default:
						break;
					}
				}
				return null;
			}
			@Override
			public String getColumnName(int column) {
				switch (column) {
				case 0:
					return "Name";
				case 1:
					return "Value";
				default:
					return super.getColumnName(column);
				}
			}
		};
		final JTable table = new JTable(model);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		return panel;
	}
	protected static void exit(int code) {
		System.exit(code);
	}
}
