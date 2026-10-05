package app.s.chzzk.extension;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public final class ChzzkSettings {
    private static SharedPreferences preferences;
    private static WeakReference<Activity> activity = new WeakReference<>(null);
    private static Object revision;
    private static Method readRevision;
    private static Method writeRevision;
    private static int revisionNumber;
    private static Object settingsAction;
    private static String[] keywords = new String[0];
    private static String[] blockedUsers = new String[0];
    private static final LinkedHashMap<String, String> history = new LinkedHashMap<>();


    private ChzzkSettings() {}

    public static void attach(Activity host) {
        activity = new WeakReference<>(host);
        if (preferences == null) {
            preferences = host.getApplicationContext().getSharedPreferences("s_chzzk", Context.MODE_PRIVATE);
            keywords = lines(preferences.getString("keywords", ""));
            blockedUsers = lines(preferences.getString("blocked_users", ""));
            revision = createRevision();
            try {
                readRevision = Class.forName("androidx.compose.runtime.State").getMethod("getValue");
                writeRevision = Class.forName("androidx.compose.runtime.MutableState").getMethod("setValue", Object.class);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("CHZZK Compose state bridge is unavailable", e);
            }
        }

    }

    public static Object createRevision() { return null; }
    public static String messageContent(Object message) { return ""; }
    public static String visibleContent(Object message) { return ""; }
    public static String messageNickname(Object message) { return ""; }
    public static String messageKind(Object message) { return ""; }
    public static String messageStatus(Object message) { return ""; }
    public static String messageRole(Object message) { return ""; }
    public static String messageId(Object message) { return ""; }
    public static long messageTime(Object message) { return 0L; }
    public static boolean isAdCard(Object card) { return "AD".equals(card); }
    public static boolean autoClaimAvailable() { return false; }
    public static Object kotlinUnit() { return null; }

    public static Object settingsClick() {
        if (settingsAction == null) {
            try {
                Class<?> function = Class.forName("kotlin.jvm.functions.Function0");
                settingsAction = Proxy.newProxyInstance(function.getClassLoader(), new Class<?>[] {function},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "invoke":
                                Activity host = activity.get();
                                if (host != null && !host.isFinishing() && !host.isDestroyed()) showSettings(host);
                                return kotlinUnit();
                            case "hashCode": return System.identityHashCode(proxy);
                            case "equals": return proxy == args[0];
                            case "toString": return "Morphe settings";
                            default: throw new UnsupportedOperationException(method.getName());
                        }
                    });
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return settingsAction;
    }

    public static List<?> filterClipCards(List<?> cards) {
        if (cards == null) return null;
        ArrayList<Object> filtered = new ArrayList<>(cards.size());
        for (Object card : cards) {
            if (card == null || !isAdCard(card)) filtered.add(card);
        }
        return filtered.size() == cards.size() ? cards : filtered;
    }

    private static void observe() {
        if (revision == null) return;
        try {
            readRevision.invoke(revision);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void changed() {
        if (revision == null) return;
        try {
            writeRevision.invoke(revision, ++revisionNumber);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean enabled(String key) {
        observe();
        return preferences != null && preferences.getBoolean(key, false);
    }

    private static int number(String key, int fallback) {
        observe();
        return preferences == null ? fallback : preferences.getInt(key, fallback);
    }

    public static float chatWidth(float original) {
        Activity host = activity.get();
        if (host == null || original <= 0) return original;
        boolean tablet = host.getResources().getConfiguration().smallestScreenWidthDp >= 600;
        int value = number(tablet ? "tablet_width" : "phone_width", 0);
        return value == 0 ? original : Math.max(120, Math.min(600, value));
    }

    public static float scaleFont(float value) {
        return value * number("font_scale", 100) / 100f;
    }

    public static float scaleLine(float value) {
        return scaleFont(value) * number("line_scale", 100) / 100f;
    }

    public static float scaleEmoji(float value) {
        return value * number("emoji_scale", 100) / 100f;
    }

    public static long scaleTextUnit(long value) {
        if ((value & 0xff00000000L) != 0x100000000L) return value;
        float scaled = scaleFont(Float.intBitsToFloat((int) value));
        return (value & 0xffffffff00000000L) | (Float.floatToRawIntBits(scaled) & 0xffffffffL);
    }

    public static long scaleBadge(long value) {
        if ((value & 0xff00000000L) != 0x100000000L) return value;
        float scaled = Float.intBitsToFloat((int) value) * number("badge_scale", 100) / 100f;
        return (value & 0xffffffff00000000L) | (Float.floatToRawIntBits(scaled) & 0xffffffffL);
    }

    public static boolean hideHeader(Object item) {
        if (item == null) return false;
        switch (item.getClass().getSimpleName()) {
            case "DonationRank": return enabled("hide_donation_rank");
            case "TongPowRank": return enabled("hide_tongpow_rank");
            case "TongPowPrediction": return enabled("hide_prediction_panel");
            case "FcOnlinePersonalized": return enabled("hide_chat_promotions");
            default: return false;
        }
    }

    public static boolean hideFollowPrompt() {
        return enabled("hide_follow_prompt");
    }

    public static boolean cleanbot(boolean original) {
        return original && !enabled("restore_cleanbot");
    }

    public static boolean autoClaim() {
        observe();
        return preferences == null || preferences.getBoolean("auto_claim", true);
    }

    public static boolean hideNickname(Object message) {
        if (!enabled("hide_nickname")) return false;
        String role = messageRole(message);
        return !"STREAMER".equals(role) && !"MANAGER".equals(role)
            && !role.endsWith("_MANAGER") && !role.endsWith("_OWNER");
    }

    public static boolean hideMessage(Object message) {
        if (message == null) return false;
        String content = messageContent(message);
        String nickname = messageNickname(message);
        String kind = messageKind(message);
        observe();
        if (matches(content, keywords, false) || matches(nickname, blockedUsers, true)) return true;
        if (("DONATION".equals(kind) || kind.startsWith("PARTY_DONATION")) && enabled("hide_donation")) return true;
        if (("SUBSCRIPTION".equals(kind) || "GIFT".equals(kind)) && enabled("hide_subscription")) return true;
        if (("SYSTEM".equals(kind) || "OPEN".equals(kind) || "WELCOME".equals(kind)) && enabled("hide_system")) return true;
        if (kind.startsWith("TONG_POW_PREDICTION") && enabled("hide_prediction")) return true;
        if ("WATCH_PARTY_PLUS".equals(kind) && enabled("hide_party")) return true;
        if ("STREAMER_SHOP_PURCHASE".equals(kind) && enabled("hide_shop")) return true;
        if (enabled("keep_history") && "USER".equals(kind) && content != null) {
            String id = messageId(message);
            if (id != null && !id.isEmpty()) {
                synchronized (history) {
                    history.put(id, time(messageTime(message)) + " " + nickname + ": " + content);
                    while (history.size() > 200) history.remove(history.keySet().iterator().next());
                }
            }
        }
        return false;
    }

    public static String decorateMessage(String original, Object message) {
        String text = original == null ? "" : original;
        if (enabled("timestamps")) text = "[" + time(messageTime(message)) + "] " + text;
        return enabled("multiline") ? "\n" + text : text;
    }

    public static String displayMessage(Object message) {
        String visible = restoreBlindText(visibleContent(message), messageContent(message), messageStatus(message));
        return decorateMessage(visible, message);
    }

    public static String restoreBlindText(String visible, String raw, String status) {
        if (enabled("restore_blind") && raw != null && !raw.isEmpty()
                && ("BLIND".equals(status) || "HIDDEN".equals(status) || "RECLAIM".equals(status))) {
            return raw;
        }
        return visible;
    }

    private static String time(long timestamp) {
        return new SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(new Date(timestamp));
    }

    private static boolean matches(String value, String[] patterns, boolean exact) {
        if (value == null) return false;
        String folded = value.toLowerCase(Locale.ROOT);
        for (String pattern : patterns) {
            if (exact ? folded.equals(pattern) : folded.contains(pattern)) return true;
        }
        return false;
    }

    private static String[] lines(String value) {
        ArrayList<String> result = new ArrayList<>();
        for (String line : value.split("\\R")) {
            String text = line.trim().toLowerCase(Locale.ROOT);
            if (!text.isEmpty() && text.length() <= 100 && result.size() < 100) result.add(text);
        }
        return result.toArray(new String[0]);
    }

    private static int dp(Context context, float value) {
        return Math.round(context.getResources().getDisplayMetrics().density * value);
    }

    private static LinearLayout panel(Context context) {
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(context, 20), dp(context, 8), dp(context, 20), dp(context, 16));
        return panel;
    }

    private static void label(LinearLayout panel, String text) {
        TextView view = new TextView(panel.getContext());
        view.setText(text);
        view.setTextSize(14);
        view.setPadding(0, dp(panel.getContext(), 12), 0, dp(panel.getContext(), 4));
        panel.addView(view);
    }

    private static void slider(LinearLayout panel, String title, String key, int min, int max, int fallback, String unit) {
        TextView value = new TextView(panel.getContext());
        int current = number(key, fallback);
        value.setText(title + ": " + (current == 0 ? "앱 기본값" : current + unit));
        value.setPadding(0, dp(panel.getContext(), 12), 0, 0);
        panel.addView(value);
        SeekBar seek = new SeekBar(panel.getContext());
        seek.setMax(max - min);
        seek.setProgress(Math.max(0, current - min));
        seek.setContentDescription(title);
        panel.addView(seek);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int next = progress + min;
                value.setText(title + ": " + next + unit);
                preferences.edit().putInt(key, next).apply();
                changed();
            }
            public void onStartTrackingTouch(SeekBar bar) {}
            public void onStopTrackingTouch(SeekBar bar) {}
        });
    }

    private static void toggle(LinearLayout panel, String title, String key, boolean fallback) {
        Switch control = new Switch(panel.getContext());
        control.setText(title);
        control.setTextSize(14);
        control.setPadding(0, dp(panel.getContext(), 8), 0, dp(panel.getContext(), 8));
        control.setChecked(preferences.getBoolean(key, fallback));
        control.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean(key, checked).apply();
            if ("keep_history".equals(key) && !checked) synchronized (history) { history.clear(); }
            changed();
        });
        panel.addView(control);
    }

    private static void action(LinearLayout panel, String text, Runnable callback) {
        Button button = new Button(panel.getContext());
        button.setText(text);
        button.setOnClickListener(view -> callback.run());
        panel.addView(button);
    }

    private static void editList(Activity host, String title, String key) {
        EditText input = new EditText(host);
        input.setText(preferences.getString(key, ""));
        input.setMinLines(5);
        input.setMaxLines(10);
        input.setGravity(Gravity.TOP);
        input.setHint("한 줄에 하나씩, 최대 100개");
        LinearLayout content = panel(host);
        content.addView(input);
        new AlertDialog.Builder(host).setTitle(title).setView(content)
            .setNegativeButton("취소", null)
            .setPositiveButton("저장", (dialog, which) -> {
                String value = input.getText().toString();
                String[] parsed = lines(value);
                preferences.edit().putString(key, String.join("\n", parsed)).apply();
                if ("keywords".equals(key)) keywords = parsed;
                else blockedUsers = parsed;
                changed();
            }).show();
    }

    private static void showHistory(Activity host) {
        TextView text = new TextView(host);
        text.setTextIsSelectable(true);
        synchronized (history) {
            text.setText(history.isEmpty() ? "기록된 채팅이 없습니다." : String.join("\n\n", history.values()));
        }
        text.setPadding(dp(host, 16), dp(host, 8), dp(host, 16), dp(host, 8));
        ScrollView scroll = new ScrollView(host);
        scroll.addView(text);
        new AlertDialog.Builder(host).setTitle("최근 표시한 채팅 · 최대 200개").setView(scroll)
            .setPositiveButton("닫기", null)
            .setNeutralButton("기록 지우기", (dialog, which) -> { synchronized (history) { history.clear(); } }).show();
    }

    private static boolean booleanKey(String key) {
        switch (key) {
            case "timestamps": case "multiline": case "hide_nickname": case "restore_cleanbot": case "restore_blind":
            case "hide_donation": case "hide_subscription": case "hide_system": case "hide_prediction":
            case "hide_party": case "hide_shop": case "hide_donation_rank": case "hide_tongpow_rank":
            case "hide_prediction_panel": case "hide_chat_promotions": case "hide_follow_prompt":
            case "auto_claim": case "keep_history":
                return true;
            default: return false;
        }
    }

    private static int[] bounds(String key) {
        switch (key) {
            case "phone_width": case "tablet_width": return new int[] {120, 600};
            case "font_scale": case "emoji_scale": case "badge_scale": return new int[] {80, 200};
            case "line_scale": return new int[] {80, 160};
            default: return null;
        }
    }

    private static Map<String, Object> parseSettings(String text) throws JSONException {
        if (text.length() > 65536) throw new IllegalArgumentException("설정 내용이 너무 큽니다.");
        JSONObject envelope = new JSONObject(text);
        Object version = envelope.opt("version");
        if (!"s-chzzk-settings".equals(envelope.optString("format"))
                || !(version instanceof Integer) || (Integer) version != 1) {
            throw new IllegalArgumentException("지원하는 치지직 설정 형식이 아닙니다.");
        }
        JSONObject settings = envelope.getJSONObject("settings");
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        java.util.Iterator<String> keys = settings.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = settings.get(key);
            int[] range = bounds(key);
            if (booleanKey(key)) {
                if (!(value instanceof Boolean)) throw new IllegalArgumentException(key + ": 켜기/끄기 값이 필요합니다.");
            } else if (range != null) {
                if (!(value instanceof Integer)) throw new IllegalArgumentException(key + ": 정수가 필요합니다.");
                int number = (Integer) value;
                if (!(key.endsWith("_width") && number == 0) && (number < range[0] || number > range[1])) {
                    throw new IllegalArgumentException(key + ": 설정 범위를 벗어났습니다.");
                }
            } else if ("keywords".equals(key) || "blocked_users".equals(key)) {
                if (!(value instanceof String)) throw new IllegalArgumentException(key + ": 텍스트가 필요합니다.");
                value = String.join("\n", lines((String) value));
            } else {
                throw new IllegalArgumentException("알 수 없는 설정: " + key);
            }
            result.put(key, value);
        }
        return result;
    }

    private static void copySettings(Activity host) {
        try {
            JSONObject envelope = new JSONObject().put("format", "s-chzzk-settings").put("version", 1)
                .put("settings", new JSONObject(preferences.getAll()));
            ClipboardManager clipboard = (ClipboardManager) host.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Morphe 치지직 설정", envelope.toString(2)));
            Toast.makeText(host, "설정을 복사했습니다.", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(host, "설정을 복사하지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private static void pasteSettings(Activity host, AlertDialog parent) {
        EditText input = new EditText(host);
        input.setMinLines(6);
        input.setMaxLines(12);
        input.setGravity(Gravity.TOP);
        input.setHint("복사한 설정 JSON을 붙여넣으세요.");
        ClipboardManager clipboard = (ClipboardManager) host.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            CharSequence copied = clip.getItemAt(0).getText();
            if (copied != null && copied.length() <= 65536) input.setText(copied);
        }
        LinearLayout content = panel(host);
        label(content, "적용하면 현재 설정이 교체됩니다. 최근 채팅 기록은 복사되지 않습니다.");
        content.addView(input);
        AlertDialog dialog = new AlertDialog.Builder(host).setTitle("설정 붙여넣기").setView(content)
            .setNegativeButton("취소", null).setPositiveButton("적용", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            try {
                Map<String, Object> settings = parseSettings(input.getText().toString());
                SharedPreferences.Editor editor = preferences.edit().clear();
                for (Map.Entry<String, Object> entry : settings.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Boolean) editor.putBoolean(entry.getKey(), (Boolean) value);
                    else if (value instanceof Integer) editor.putInt(entry.getKey(), (Integer) value);
                    else editor.putString(entry.getKey(), (String) value);
                }
                editor.apply();
                keywords = lines(preferences.getString("keywords", ""));
                blockedUsers = lines(preferences.getString("blocked_users", ""));
                if (!preferences.getBoolean("keep_history", false)) synchronized (history) { history.clear(); }
                changed();
                dialog.dismiss();
                parent.dismiss();
                showSettings(host);
                Toast.makeText(host, "설정을 적용했습니다.", Toast.LENGTH_SHORT).show();
            } catch (JSONException | IllegalArgumentException e) {
                input.setError("설정을 적용하지 않았습니다. " + e.getMessage());
            }
        }));
        dialog.show();
    }

    public static void showSettings(Activity host) {
        final AlertDialog[] settings = new AlertDialog[1];
        LinearLayout content = panel(host);
        label(content, "채팅 · 변경 즉시 저장");
        slider(content, "휴대폰 채팅 폭", "phone_width", 120, 600, 0, "dp");
        slider(content, "태블릿 채팅 폭", "tablet_width", 120, 600, 0, "dp");
        label(content, "채팅과 영상이 나란히 표시될 때 적용됩니다. 앱의 화면 너비 제한은 유지됩니다.");
        slider(content, "채팅 글자 크기", "font_scale", 80, 200, 100, "%");
        slider(content, "이모티콘 크기", "emoji_scale", 80, 200, 100, "%");
        slider(content, "배지 크기 (글자 배율에 추가 적용)", "badge_scale", 80, 200, 100, "%");
        slider(content, "줄 간격", "line_scale", 80, 160, 100, "%");
        toggle(content, "채팅 시각 표시", "timestamps", false);
        toggle(content, "닉네임과 본문 줄 분리", "multiline", false);
        toggle(content, "닉네임·배지 숨기기 (스트리머·매니저 제외)", "hide_nickname", false);
        toggle(content, "클린봇 원문 표시 (앱에 원문이 남아 있을 때)", "restore_cleanbot", false);
        toggle(content, "임시차단·블라인드 채팅 원문 표시", "restore_blind", false);
        label(content, "앱에 남아 있는 원문만 표시합니다. 서버에서 원문을 보내지 않은 메시지는 복원할 수 없습니다.");
        label(content, "채팅 정리");
        toggle(content, "후원·파티 후원 메시지 숨기기", "hide_donation", false);
        toggle(content, "구독·선물 메시지 숨기기", "hide_subscription", false);
        toggle(content, "입장·환영·시스템 메시지 숨기기", "hide_system", false);
        toggle(content, "승부예측 메시지 숨기기", "hide_prediction", false);
        toggle(content, "같이보기 안내 숨기기", "hide_party", false);
        toggle(content, "스트리머샵 구매 메시지 숨기기", "hide_shop", false);
        toggle(content, "채팅 상단 후원 순위 숨기기", "hide_donation_rank", false);
        toggle(content, "채팅 상단 통나무 순위 숨기기", "hide_tongpow_rank", false);
        toggle(content, "채팅 상단 승부예측 패널 숨기기", "hide_prediction_panel", false);
        toggle(content, "채팅 상단 게임 프로모션 숨기기", "hide_chat_promotions", false);
        toggle(content, "팔로우 권유 팝업 숨기기", "hide_follow_prompt", false);
        action(content, "숨길 단어 설정", () -> editList(host, "본문에 포함된 단어 숨기기", "keywords"));
        action(content, "숨길 닉네임 설정", () -> editList(host, "닉네임이 정확히 일치하는 채팅 숨기기", "blocked_users"));
        label(content, "통나무·채팅 기록");
        if (autoClaimAvailable()) toggle(content, "통나무 파워 자동 수령", "auto_claim", true);
        toggle(content, "화면에 표시한 최근 채팅 기억하기", "keep_history", false);
        label(content, "최근 채팅은 이 앱 실행 중 메모리에만 보관합니다.");
        action(content, "최근 채팅 보기", () -> showHistory(host));
        label(content, "설정 관리");
        action(content, "설정 복사", () -> copySettings(host));
        action(content, "설정 붙여넣기", () -> pasteSettings(host, settings[0]));
        action(content, "설정 초기화", () -> new AlertDialog.Builder(host)
            .setMessage("채팅 설정을 앱 기본값으로 되돌릴까요?")
            .setNegativeButton("취소", null).setPositiveButton("초기화", (dialog, which) -> {
                preferences.edit().clear().apply();
                keywords = new String[0];
                blockedUsers = new String[0];
                synchronized (history) { history.clear(); }
                changed();
                settings[0].dismiss();
                showSettings(host);
            }).show());
        ScrollView scroll = new ScrollView(host);
        scroll.addView(content);
        settings[0] = new AlertDialog.Builder(host).setTitle("Morphe").setView(scroll).setPositiveButton("닫기", null).show();
    }
}
