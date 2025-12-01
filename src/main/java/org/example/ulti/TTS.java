package org.example.ulti;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TTS {
    private static Voice voice;

    public static void init() {
        if (voice != null) return;

        try {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");//Báo cho TTS biết là giọng nói nằm ở đâu

            //VoiceManager: Quản lý tất cả giọng nói có sẵn trong FreeTTS
            //getInstance(): Lấy đối tượng duy nhất (Singleton) → Không tạo nhiều lần
            VoiceManager vm = VoiceManager.getInstance(); // khởi tạo biến quản lý giọng nói. và lấy
            voice = vm.getVoice("kevin16");// Lấy giọng cụ thể theo tên

            if(voice !=null)
            {
                voice.allocate(); //Khởi tạo tài nguyên (bộ nhớ, âm thanh, engine)
                voice.setRate(130);// Chỉnh tốc độ giọng đọc
                System.out.println("TTS: Sẵn sàng!");
            }
            else
                System.out.println("TTS: Không tìm thấy giọng kevin16 ");

        } catch (Exception e) {
            System.err.println("TTS: Lỗi khởi tạo - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void speak(String text)
    {
        if (voice != null && text != null && !text.trim().isEmpty())
        {
            new Thread(() -> voice.speak(text.trim())).start(); // Tạo 1 luồng(Thread) mới để đọc , không ảnh hưởng đến các phần chạy khác
        }
    }

    public static void close()
    {
        if(voice != null)
        {
            try {
                voice.deallocate(); // Đóng tài nguyên sau khi tắt app
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
