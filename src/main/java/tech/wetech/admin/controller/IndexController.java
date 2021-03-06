package tech.wetech.admin.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tech.wetech.admin.common.Constants;
import tech.wetech.admin.model.system.entity.Resource;
import tech.wetech.admin.service.system.ResourceService;
import tech.wetech.admin.service.system.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author cjbi
 */
@Controller
public class IndexController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        Set<String> permissions = userService.findPermissions(username);
        List<Resource> menus = resourceService.findMenus(permissions);
        StringBuilder dom = new StringBuilder();
        getMenuTree(menus, Constants.MENU_ROOT_ID, dom);
        model.addAttribute(Constants.MENU_TREE, dom);
        return "base/main";
    }

    private List<Resource> getMenuTree(List<Resource> source, Long pid, StringBuilder dom) {
        List<Resource> target = getChildResourceByPid(source, pid);
        target.forEach(res -> {
            dom.append("<li class='treeview'>");
            dom.append("<a href='" + res.getUrl() + "'>");
            dom.append("<i class='" + res.getIcon() + "'></i>");
            dom.append("<span>" + res.getName() + "</span>");
            if (Constants.SHARP.equals(res.getUrl())) {
                dom.append("<span class='pull-right-container'><i class='fa fa-angle-left pull-right'></i> </span>");
            }
            dom.append("</a>");
            dom.append("<ul class='treeview-menu'>");
            res.setChildren(getMenuTree(source, res.getId(), dom));
            dom.append("</ul>");
            dom.append("</li>");
        });
        return target;
    }

    private List<Resource> getChildResourceByPid(List<Resource> source, Long pId) {
        List<Resource> child = new ArrayList<>();
        source.forEach(res -> {
            if (pId.equals(res.getParentId())) {
                child.add(res);
            }
        });
        return child;
    }

}
