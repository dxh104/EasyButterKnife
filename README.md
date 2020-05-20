# EasyButterKnife

Activity绑定View

项目配置
```
allprojects {

     repositories {
    
          maven { url 'https://jitpack.io' }
      
     }
   
   }
```
 
添加依赖

```
implementation 'com.github.dxh104.EasyButterKnife:easy_butterknife:1.0.0'

implementation 'com.github.dxh104.EasyButterKnife:easy_butterknife-annotations:1.0.0'

annotationProcessor 'com.github.dxh104.EasyButterKnife:easy_butterknife-processer:1.0.0'
```

example {

    @BindView(R.id.textView)
    TextView textView;
    private UnBinder unBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unBinder = ButterKnife.bind(this);
        textView.setText("BindView绑定成功");
    }

    @Override
    protected void onDestroy() {
        unBinder.unbind();
        super.onDestroy();
    }
    
}

