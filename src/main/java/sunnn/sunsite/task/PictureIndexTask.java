package sunnn.sunsite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sunnn.sunsite.dao.PictureDao;
import sunnn.sunsite.entity.Picture;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Character.UnicodeBlock.of;

@Component
public class PictureIndexTask {

    private static Logger log = LoggerFactory.getLogger(PictureIndexTask.class);

    @Resource
    private PictureDao dao;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void submit(long collection, int count) {
        Task t = new Task(collection, count);

        executor.submit(t);
    }

    private class Task implements Runnable {

        private static final int BLANK = 1;

        private static final int SYMBOL = 2;

        private static final int NUMBER = 4;

        private static final int LETTER = 8;

        private static final int CHARACTER = 16;

        /**
         * 空格、不换行空格
         */
        private final char[] BLANK_CHAR = {0x0020, 0x00A0};

        private long collection;

        private int count;

        public Task(long collection, int count) {
            this.collection = collection;
            this.count = count;
        }

        @Override
        public void run() {
            List<Picture> pictures = getCurrentPictureList();

            int startIndex = getStartIndex(pictures);
            if (startIndex == -1)
                return;

            PriorityQueue<Picture> queue = generateQueue(pictures.size());
            // 插入新记录
            for (int i = startIndex; i < pictures.size(); ++i)
                queue.add(pictures.get(i));

            int newIndex = Integer.MAX_VALUE;
            // 将已有记录从后往前插入，寻找第一个需要修改index的
            for (int i = startIndex - 1; i >= 0; --i) {
                queue.add(pictures.get(i));
                newIndex = queue.peek().getIndex();
                if (newIndex != Integer.MAX_VALUE)
                    break;  // 找到了
            }

            // 更新index
            if (newIndex == Integer.MAX_VALUE)
                newIndex = 1;

            while (!queue.isEmpty()) {
                Picture p = queue.poll();
                dao.updateIndex(newIndex++, p.getSequence());
            }
        }

        private List<Picture> getCurrentPictureList() {
            while (dao.countByCollection(collection) < count) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }

            return dao.findAllByCollection(collection);
        }

        /**
         * 找新插入的数据
         * 先找头和尾，再看看中间，中间有从头找，没有从后找
         */
        private int getStartIndex(List<Picture> pictures) {
            if (pictures.isEmpty())
                return -1;

            if (pictures.get(0).getIndex() == Integer.MAX_VALUE)
                return 0;

            if (pictures.get(pictures.size() - 1).getIndex() == Integer.MAX_VALUE
                    && pictures.get(pictures.size() - 2).getIndex() != Integer.MAX_VALUE)
                return pictures.size() - 1;

            int mid = pictures.size() / 2;
            if (pictures.get(mid).getIndex() == Integer.MAX_VALUE) {
                for (int i = 1; i < mid; ++i) {
                    if (pictures.get(i).getIndex() == Integer.MAX_VALUE)
                        return i;
                }
                return mid;
            } else {
                for (int i = pictures.size() - 2; i >= mid; --i) {
                    if (pictures.get(i).getIndex() != Integer.MAX_VALUE)
                        return i + 1;
                }
                return -1;  // no result
            }
        }

        private PriorityQueue<Picture> generateQueue(int size) {
            return new PriorityQueue<>(size, new Comparator<Picture>() {
                @Override
                public int compare(Picture o1, Picture o2) {
                    String s1 = o1.getName();
                    String s2 = o2.getName();

                    int index1 = 0, index2 = 0, lastIndex1 = 0, lastIndex2 = 0;

                    while (index1 < s1.length() && index2 < s2.length()) {

                        int type1 = charHandler(s1.charAt(index1++));
                        while (index1 < s1.length() && type1 == charHandler(s1.charAt(index1)))
                            ++index1;

                        int type2 = charHandler(s2.charAt(index2++));
                        while (index2 < s2.length() && type2 == charHandler(s2.charAt(index2)))
                            ++index2;

                        if (type1 > type2)
                            return 1;
                        else if (type1 < type2)
                            return -1;
                        else {
                            String aContent = s1.substring(lastIndex1, index1);
                            String bContent = s2.substring(lastIndex2, index2);

                            int r;
                            if (type1 == NUMBER) {
                                BigDecimal aNum = new BigDecimal(aContent);
                                BigDecimal bNum = new BigDecimal(bContent);

                                r = aNum.compareTo(bNum);
                                if (r == 0) {
                                    // 相同的数字串需要确认下是否为0串
                                    // 0串的话长度短的取小
                                    if (aNum.intValue() == 0)
                                        r = aContent.compareTo(bContent);
                                }
                            } else {
                                r = aContent.compareToIgnoreCase(bContent);
                            }

                            if (r != 0)
                                return r;
                        }
                        lastIndex1 = index1;
                        lastIndex2 = index2;
                    }

                    if (index1 < s1.length())
                        return 1;
                    if (index2 < s2.length())
                        return -1;
                    return 0;
                }

                /**
                 * 判断一个字符的归属
                 * 这里使用UnicodeBlock来判断一个字符归属
                 * <p>
                 * 分为五种情况：
                 * <p>
                 * 1.空格
                 * <p>
                 * 2.符号：包括了ASCII码中的符号、中文符号、Latin-1中的符号
                 * <p>
                 * 3.数字
                 * <p>
                 * 4.大小写英文字母
                 * <p>
                 * 5.其他符号：比如汉字，拉丁字母，日文假名等
                 * <p>
                 * 别想了这个没有异常处理，往这里丢文件名中不可能有的字符会直接返回CHARACTER
                 */
                private int charHandler(char c) {
                    /*
                        空格判断
                     */
                    for (char blank : BLANK_CHAR) {
                        if (c == blank)
                            return BLANK;
                    }

                    Character.UnicodeBlock ub = of(c);
                    /*
                        BASIC LATIN
                        数字/英文字符/英文字母
                     */
                    if (ub == Character.UnicodeBlock.BASIC_LATIN) { // 基本拉丁字符（？就是ASCII码里的东西
                        if (c >= 'A' && c <= 'Z'
                                || c >= 'a' && c <= 'z') {
                            // 英文字母
                            return LETTER;
                        }
                        if (c >= '0' && c <= '9') {
                            // 数字
                            return NUMBER;
                        }
                        // 英文字符
                        return SYMBOL;
                    }
                    /*
                        中文符号
                     */
                    if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION    // 通常标点符号(“”…
                            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS   // 全角、半角的
                            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION // CJK标点符号(、《》
                            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS // CJK兼容性格式？？？(主要是给竖写方式使用的符号
                            || ub == Character.UnicodeBlock.VERTICAL_FORMS)  // 竖直格式(主要是一些竖着写的标点符号
                        return SYMBOL;
                    /*
                        拉丁字母-1 辅助
                     */
                    if (ub == Character.UnicodeBlock.LATIN_1_SUPPLEMENT) {
                        // 符号
                        if (c >= '¡' && c <= '¿')
                            return SYMBOL;
                        if (c == '×' || c == '÷')
                            return SYMBOL;
                        // 拉丁字母
                        return CHARACTER;
                    }
                    /*
                        其他
                        汉字/日文/etc.
                     */
                    return CHARACTER;
                }
            });
        }
    }
}
