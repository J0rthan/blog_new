# Spring MVC diagram

Spring MVC request flow:

![How Java Spring MVC Works](/Users/lkw/Desktop/Blog/项目指南/图片资源\(不用管\)/Spring-MVC-Flow-Diagram.png)

1. Client requests for a page by specifying the web URL for the page.
2. Client request is intercepted by the **Dispatcher Servlet** also known as **Front Controller**. Dispatcher Servlet is a servlet specified in **web.xml** file.
3. Dispatcher Servlet uses **URL Mapping Handler** to find out the relevant controller class to which request should be passed for subsequent processing.
4. Once Dispatcher Servlet has identified the controller to be considered, it passes the client request to the **controller**.
5. The controller class is the main class controlling the business logic flow once request that has been dispatched by dispatcher servlet. This class will implement the methods for different type of http requests and all logic to call service layer methods will reside in this controller class. This controller class will also be responsible for returning the **ModelAndView** object back to the dispatcher servlet.

Controller class is annotated by @Controller annotation

6. After receiving ModelAndView object from the controller, Dispatcher Servlet sends model object to view resolver to get the name of the view which needs to be rendered.
7. Once the view to be rendered has been identified, Dispatcher Servlet passes model object to the view. Model object contains the data which needs to be displayed in the view. View will be rendered with the model data.
8. This view is returned to the client and client can see the view and associated data on his browser.

