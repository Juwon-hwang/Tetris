package src.kr.ac.jbnu.se.tetris;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FirebaseUtil {

    private static Firestore db;



    public static void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("tetris-28f0f-firebase-adminsdk-rladg-0763c4b60f.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) throws ExecutionException, InterruptedException {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.id());
        userMap.put("password", user.password());
        userMap.put("score", user.score());

        db.collection("users").document(user.id()).set(userMap).get();
    }
    public static void addScore(String userId, int newScore) throws ExecutionException, InterruptedException {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("score", newScore);

        db.collection("users").document(userId).update(updateData).get();
    }

    public static String validateUserPassword(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = db.collection("users").document(id).get().get();
        if(documentSnapshot.exists()){
            return documentSnapshot.getString("password");
        }
        else{
            return null;
        }
    }
    public static String validateUserId(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = db.collection("users").document(id).get().get();
        if(documentSnapshot.exists()){
            return documentSnapshot.getString("id");
        }
        else{
            return null;
        }
    }
    public static int getUserScore(String id) {
        try {
            ApiFuture<DocumentSnapshot> future = db.collection("users").document(id).get();
            DocumentSnapshot documentSnapshot = future.get(); // get() 메서드로 결과를 동기적으로 기다림
            if (documentSnapshot.exists()) {
                Long score = documentSnapshot.getLong("score");
                return score != null ? score.intValue() : 0;
            } else {
                return 0;
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            return 0; // 예외 발생 시 기본값 0 반환
        }
    }
    public static ArrayList<User> getTopScores(int limit) {
        ArrayList<User> allScores = getAllUserScores(); // 모든 사용자의 스코어 가져오기

        // 내림차순으로 정렬
        allScores.sort(Comparator.comparing(User::score).reversed());

        // 최고 점수 순위에서 10위까지 반환
        return new ArrayList<>(allScores.subList(0, Math.min(limit, allScores.size())));
    }
    public static ArrayList<User> getAllUserScores() {
        ArrayList<User> allScores = new ArrayList<>();
        CollectionReference usersCollection = db.collection("users");

        ApiFuture<QuerySnapshot> query = usersCollection.get();
        try {
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                String id = document.getString("id");
                Long score = document.getLong("score");

                if (id != null && score != null) {
                    // 이 부분을 수정
                    User user = new User(id, "password", score.intValue());
                    allScores.add(user);
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        return allScores;
    }

}
