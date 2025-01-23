package mr.demonid.gui.view.infopanels.controls;

import java.awt.*;

/**
 * Менеджер расположения компонентов правой панели (вертикальный),
 * поскольку VBox ведёт себя немного не так как нужно
 */
public class InfoLayout implements LayoutManager {

    private final int gap;

    public InfoLayout(int gap)
    {
        this.gap = gap;
    }

    /**
     * Сигнал на расположение компонентов в контейнере
     * @param parent the container to be laid out
     */
    @Override
    public void layoutContainer(Container parent) {
        Dimension st = parent.getSize();
        Component[] components = parent.getComponents();
        // вычисляем макс. ширину
        int width = getMaxWidth(parent);
        int y = gap;
        for (int i = 0; i < components.length-1; i++) {
            Component comp = components[i];
            Dimension size = comp.getPreferredSize();
            comp.setBounds(gap,y, width,size.height); // задаём абсолютные координаты и размер компоненту
            y += size.height + gap;
        }
        // последний компонент расширяем до низу
        int height = st.height - y-gap;
        components[components.length-1].setBounds(gap,y,width, height);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return calcSize(parent);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return calcSize(parent);
    }

    /*
        Это ненужные нам методы
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {}


    /**
     * Определяет максимальную ширину для компонентов
     * @param parent
     * @return
     */
    private int getMaxWidth(Container parent) {
        // вычисляем макс. ширину
        int width = 0;
        for (Component comp : parent.getComponents()) {
            Dimension size = comp.getPreferredSize();
            if (size.width > width)
                width = size.width;;
        }
        return width;
    }
    

    private Dimension calcSize(Container parent) {
        Dimension size = new Dimension();

        int width = getMaxWidth(parent);    // вычисляем ширину контейнера
        // вычисляем высоту контейнера
        int height = 0;
        for (Component comp : parent.getComponents()) {
            height += gap + comp.getHeight();
        }
        size.width = width + gap*2;
        size.height = height;

        return size;
    }

}
