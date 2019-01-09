package Construction.Data;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HttpPostor {

    public static void sentPost(String url, String id, BufferedWriter wt, HashMap map) throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(6000)
                .setConnectTimeout(6000)
                .setSocketTimeout(6000)
                .build();
        HttpPost httpPost = new HttpPost(url);

        /**
         List<NameValuePair> nvps = new ArrayList<NameValuePair>();
         nvps.add(new BasicNameValuePair("auto_id","417629251"));
         nvps.add(new BasicNameValuePair("count", "83"));
         nvps.add(new BasicNameValuePair("title","我是谁"));
         httpPost.setEntity(new UrlEncodedFormEntity(nvps));
         */
        String request = "{\"message\":[{\"auto_id\":" + id + ", \"count\":91, \"title\":\"我是谁\"}]}";
        StringEntity requestEntity = new StringEntity(request);
        //requestEntity.setContentType("application/json");
        httpPost.setEntity(requestEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try{
            System.out.println("状态： \t" + response.getStatusLine());

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null){
                System.out.println("类型：\t" + httpEntity.getContentType());
                System.out.println("长度：\t" + httpEntity.getContentLength());
                System.out.println("编码： \t" + httpEntity.getContentEncoding());
                //System.out.println("内容：\t" + EntityUtils.toString(httpEntity,"UTF-8"));
                String s = EntityUtils.toString(httpEntity,"UTF-8");
                JSONObject m = new JSONObject(s);
                JSONArray jsonArray = m.getJSONArray(id);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String org = jsonObj.get("ORG").toString();
                    String per = jsonObj.get("PER").toString();

                    if (org.length() > 0 && per.length() > 0){
                        String ent = org + "," + per;
                        if (!map.containsKey(org)){
                            wt.write(ent + "\r\n");
                            System.out.println(ent);
                            map.put(org,per);
                        }
                    }
                    //wt.write("ORG: " + jsonObj.get("ORG") + "\r\n");
                    //wt.write("PER: " + jsonObj.get("PER") + "\r\n");
                    //wt.write("title: " + jsonObj.get("title").toString() + "\r\n");
                    //wt.write("speak_action: " + jsonObj.get("speak_action") + "\r\n");
                    //wt.write("speack_content: " + jsonObj.get("speak_content") + "\r\n");

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            response.close();
            httpClient.close();
        }
    }

    public static void main(String[] args) throws Exception{
        String url = "http://192.168.1.115:8989/opinion";

        File readFile = new File("C:\\Users\\admin\\Desktop\\auto_id.txt");
        File writeFile = new File("C:\\Users\\admin\\Desktop\\entity.txt");
        BufferedReader br = new BufferedReader(new FileReader(readFile));
        BufferedWriter wr = new BufferedWriter(new FileWriter(writeFile));
        String id;
        int count = 1;
        HashMap<String,String> map = new HashMap<>();
        while((id = br.readLine()) != null && count < 20){
            System.out.println(id);
            sentPost(url,id,wr,map);
            count ++;
        }
        wr.close();

    }

}
