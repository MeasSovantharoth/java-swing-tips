// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public final class MainPanel extends JPanel {
  private final JPanel panel = new JPanel(new BorderLayout(2, 2));
  private final JTextArea textArea = new JTextArea();
  private final JButton nb = new JButton("NORTH");
  private final JButton sb = new JButton("SOUTH");
  private final JButton wb = new JButton("WEST");
  private final JButton eb = new JButton("EAST");
  private final JScrollPane scroll = new JScrollPane(textArea);
  private final Box box = Box.createHorizontalBox();
  private final JCheckBox check = new JCheckBox("setEditable", true);

  private MainPanel() {
    super(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    panel.add(scroll);
    panel.add(nb, BorderLayout.NORTH);
    panel.add(sb, BorderLayout.SOUTH);
    panel.add(wb, BorderLayout.WEST);
    panel.add(eb, BorderLayout.EAST);

    setFocusTraversalPolicyProvider(true);
    // frame.setFocusTraversalPolicy(policy);
    // setFocusTraversalPolicy(policy);
    // setFocusCycleRoot(true);

    FocusTraversalPolicy policy0 = getFocusTraversalPolicy();
    FocusTraversalPolicy policy1 = new CustomFocusTraversalPolicy(Arrays.asList(eb, wb, sb, nb));
    FocusTraversalPolicy policy2 = new LayoutFocusTraversalPolicy() {
      @Override protected boolean accept(Component c) {
        return c instanceof JTextComponent ? ((JTextComponent) c).isEditable() : super.accept(c);
      }
    };

    ButtonGroup bg = new ButtonGroup();
    box.setBorder(BorderFactory.createTitledBorder("FocusTraversalPolicy"));
    Stream.of(new FocusTraversalPolicyChangeAction("Default", policy0),
              new FocusTraversalPolicyChangeAction("Custom", policy1),
              new FocusTraversalPolicyChangeAction("Layout", policy2))
        .map(JRadioButton::new)
        .forEach(rb -> {
          bg.add(rb);
          box.add(rb);
          box.add(Box.createHorizontalStrut(3));
        });
    bg.getElements().nextElement().setSelected(true);
    box.add(Box.createHorizontalGlue());

    check.setHorizontalAlignment(SwingConstants.RIGHT);
    check.addActionListener(e -> {
      textArea.setEditable(check.isSelected());
      debugPrint();
    });
    add(panel);
    add(box, BorderLayout.NORTH);
    add(check, BorderLayout.SOUTH);
    setPreferredSize(new Dimension(320, 240));
    EventQueue.invokeLater(this::debugPrint);
  }

  protected final class FocusTraversalPolicyChangeAction extends AbstractAction {
    private final FocusTraversalPolicy policy;

    public FocusTraversalPolicyChangeAction(String name, FocusTraversalPolicy p) {
      super(name);
      this.policy = p;
    }

    @Override public void actionPerformed(ActionEvent e) {
      setFocusTraversalPolicy(policy);
      debugPrint();
    }
  }

  public void debugPrint() {
    Container w = getTopLevelAncestor();
    textArea.setText(String.join("\n",
        debugString("JFrame", w),
        debugString("this", this),
        debugString("JPanel", panel),
        debugString("Box", box),
        debugString("JScrollPane", scroll),
        debugString("JTextArea", textArea),
        debugString("eb", eb)));
  }

  private static String debugString(String label, Container c) {
    return String.join("\n",
        "---- " + label + " ----",
        "  isFocusCycleRoot: " + c.isFocusCycleRoot(),
        "  isFocusTraversalPolicySet: " + c.isFocusTraversalPolicySet(),
        "  isFocusTraversalPolicyProvider: " + c.isFocusTraversalPolicyProvider());
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class CustomFocusTraversalPolicy extends FocusTraversalPolicy {
  private final List<? extends Component> order;

  protected CustomFocusTraversalPolicy(List<? extends Component> order) {
    super();
    this.order = order;
  }

  @Override public Component getFirstComponent(Container focusCycleRoot) {
    return order.get(0);
  }

  @Override public Component getLastComponent(Container focusCycleRoot) {
    return order.get(order.size() - 1);
  }

  @Override public Component getComponentAfter(Container focusCycleRoot, Component cmp) {
    int i = order.indexOf(cmp);
    return order.get((i + 1) % order.size());
  }

  @Override public Component getComponentBefore(Container focusCycleRoot, Component cmp) {
    int i = order.indexOf(cmp);
    return order.get((i - 1 + order.size()) % order.size());
  }

  @Override public Component getDefaultComponent(Container focusCycleRoot) {
    return order.get(0);
  }
}
