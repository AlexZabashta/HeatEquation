import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class HeatSolutionVisualizer extends JFrame {

	static HeatEquationSolver[] solovers = new HeatEquationSolver[] { 
        new DownstreamExplicit(), 
        new DownstreamImplicit(),  
        new UpstreamExplicit(), 
        new UpstreamImplicit(),
        new Leapfrog()
    };

	BufferedImage canvas = new BufferedImage(42, 23, BufferedImage.TYPE_INT_RGB);
	JLabel graph = new JLabel();

	int s = solovers.length, textH = 23;
	Checkbox[] checkbox = new Checkbox[s];
	Color[] color = new Color[s];

	String[] varName = new String[] { "T length", "kappa", "u", "dt", "dx" };
	JTextField[] var = new JTextField[varName.length];

	Component[][] component = new Component[3][];

	double[][][] f = new double[0][0][0];

	Map<Integer, Integer> cliks = new TreeMap<Integer, Integer>();

	Random rnd = new Random();
	int t = 0, maxTime = 0;

	double[] curve;

	public void onResize() {
		int ch = Math.max(getHeight() - textH * (component.length + 2), textH);
		int cw = Math.max(getWidth(), textH);
		curve = new double[cw];
		canvas = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_RGB);
		graph.setBounds(0, 0, cw, ch);
		graph.setIcon(new ImageIcon(canvas));

		for (int i = 0; i < component.length; i++) {
			for (int j = 0; j < component[i].length; j++) {
				int w = Math.max(textH, cw / component[i].length);
				component[i][j].setBounds(j * w, ch + i * textH, w, textH);
			}
		}
		draw();
	}

	public void draw() {
		int w = canvas.getWidth(), h = canvas.getHeight();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				canvas.setRGB(x, y, 0);
			}
		}

		Graphics g = canvas.getGraphics();
		if (cliks.isEmpty()) {
			if (f.length > 0) {
				for (int i = 0; i < s; i++) {
					if (0 <= t && t < f[i].length) {
						double[] cur = f[i][t];
						int m = cur.length;
						g.setColor(color[i]);
						for (int j = 1; j < m; j++) {
							int fx = ((j - 1) * w) / m;
							int tx = (j * w) / m;
							g.drawLine(fx, h - 1 - (int) cur[j - 1], tx, h - 1 - (int) cur[j]);
						}

					}
				}
			}
		} else {
			g.setColor(Color.WHITE);
			int lastX = 0, lastY = h - 1;
			for (Entry<Integer, Integer> p : cliks.entrySet()) {
				int curX = p.getKey(), curY = p.getValue();
				g.drawLine(lastX, lastY, curX, curY);
				lastX = curX;
				lastY = curY;
			}
			g.drawLine(lastX, lastY, w, h - 1);
		}
		repaint();
	}

	void reCalc() {
		try {
			if (cliks.isEmpty()) {
				throw new Exception("f is empty" + System.currentTimeMillis());
			}
			int n = Integer.parseInt(var[0].getText());

			if (2 > n || n < 16) {
				throw new Exception("2 > n || n < 16");
			}
			maxTime = n;

			double k = Double.parseDouble(var[1].getText());
			double u = Double.parseDouble(var[2].getText());
			double dt = Double.parseDouble(var[3].getText());
			double dx = Double.parseDouble(var[4].getText());

			int curW = canvas.getWidth();
			double curH = canvas.getHeight();
			double[] l = new double[curW];

			Entry<Integer, Integer> last = null;

			for (Entry<Integer, Integer> cur : cliks.entrySet()) {
				if (last != null) {
					int xl = cur.getKey() - last.getKey();
					double yl = cur.getValue() - last.getValue();

					int sx = last.getKey();
					double sy = last.getValue();

					for (int x = 0; x <= xl; x++) {
						double y = sy + (double) x * yl / xl;

						int i = (x + sx);
						if (0 < i && i < curW - 1) {
							l[i] = curH - y - 1;
						}
					}
				}
				last = cur;

			}

			f = new double[s][][];
			for (int i = 0; i < s; i++) {
				f[i] = solovers[i].solve(l, n, k, u, dt, dx);
			}

			cliks.clear();
			t = 0;
			draw();
			setTitle("Recalc " + n);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public HeatSolutionVisualizer() {
		this.setLayout(null);

		Container content = getContentPane();
		content.add(graph);

		{
			component[0] = new Component[s];
			for (int i = 0; i < s; i++) {
				color[i] = new Color(rnd.nextInt(150) + 56, rnd.nextInt(150) + 56, rnd.nextInt(150) + 56);
				checkbox[i] = new Checkbox(solovers[i].getClass().getName(), null, true);
				checkbox[i].setBackground(color[i]);
				content.add(component[0][i] = checkbox[i]);
			}
		}
		{
			component[1] = new Component[varName.length];

			for (int i = 0; i < varName.length; i++) {
				JTextField name = new JTextField();
				name.setText(varName[i]);
				name.setEditable(false);
				content.add(component[1][i] = name);
			}
		}
		{
			component[2] = new Component[var.length];
            var[0] = new JTextField("1000");
            var[1] = new JTextField("1");
            var[2] = new JTextField("1");
            var[3] = new JTextField("0.1");
            var[4] = new JTextField("1");
			for (int i = 0; i < varName.length; i++) {
				content.add(component[2][i] = var[i]);
			}
		}

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onResize();
			}
		});

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && (e.getKeyCode() == 37 || e.getKeyCode() == 39)) {
					if (maxTime > 0) {
						if (e.getKeyCode() == 37) {
							if (--t < 0) {
								t += maxTime;
							}
						} else {
							if (++t >= maxTime) {
								t -= maxTime;
							}
						}
						setTitle(t + "  " + maxTime);
						draw();
					}
				}
				return false;
			}
		});

		graph.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1) {
					setTitle("Add new point: " + e.getX() + ", " + e.getY());
					cliks.put(e.getX(), e.getY());
					draw();
				}
				if (e.getButton() == 3) {
					reCalc();
				}
			}
		});

		this.setSize(640, 480);
	}

	static final long serialVersionUID = 0xE1A;

	public static void main(String[] args) {
		HeatSolutionVisualizer hsv = new HeatSolutionVisualizer();
		hsv.setVisible(true);
		hsv.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
