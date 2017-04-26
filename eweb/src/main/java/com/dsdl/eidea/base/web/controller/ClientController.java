package com.dsdl.eidea.base.web.controller;

import com.dsdl.eidea.base.def.ActivateDef;
import com.dsdl.eidea.base.entity.bo.ClientBo;
import com.dsdl.eidea.base.entity.po.ClientPo;
import com.dsdl.eidea.base.service.ClientService;
import com.dsdl.eidea.base.web.vo.UserResource;
import com.dsdl.eidea.core.web.controller.BaseController;
import com.dsdl.eidea.core.web.def.WebConst;
import com.dsdl.eidea.core.web.result.JsonResult;
import com.dsdl.eidea.core.web.result.def.ErrorCodes;
import com.dsdl.eidea.core.web.util.SearchHelper;
import com.dsdl.eidea.core.web.vo.PagingSettingResult;
import com.googlecode.genericdao.search.Search;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by 刘大磊 on 2016/12/13 10:50.
 */
@Controller
@RequestMapping("/base/client")
public class ClientController extends BaseController {
    private static final String URI = "sys_client";
    @Autowired
    private ClientService clientService;

    @RequestMapping(value = "/showList", method = RequestMethod.GET)
    @RequiresPermissions("view")
    public ModelAndView showList() {
        ModelAndView modelAndView = new ModelAndView("/base/client/client");
        modelAndView.addObject(WebConst.PAGING_SETTINGS, PagingSettingResult.getDefault());
        modelAndView.addObject(WebConst.PAGE_URI, URI);

        return modelAndView;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @RequiresPermissions("view")
    public JsonResult<List<ClientBo>> list(HttpSession session) {
        Search search = SearchHelper.getSearchParam(URI, session);
        List<ClientBo> clientBoList = clientService.getClientList(search);
        return JsonResult.success(clientBoList);
    }

    @RequiresPermissions("view")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult<ClientBo> get(Integer id) {
        ClientBo clientBo = null;
        if (id == null) {
            return JsonResult.fail(ErrorCodes.BUSINESS_EXCEPTION.getCode(), getMessage("common.errror.get_object", getLabel("client.title")));
        } else {
            clientBo = clientService.getClientBo(id);
        }
        return JsonResult.success(clientBo);
    }

    @RequiresPermissions("add")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult<ClientBo> create() {
        ClientBo clientBo = new ClientBo();
        clientBo.setIsactive(ActivateDef.INACTIVATED.getKey());
        return JsonResult.success(clientBo);
    }

    /**
     * @param clientBo
     * @return
     */
    @RequiresPermissions("add")
    @RequestMapping(value = "/saveForCreated", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult<ClientBo> saveForCreate(@Validated @RequestBody ClientBo clientBo) {
        if (clientService.findExistClient(clientBo.getNo())) {
            return JsonResult.fail(ErrorCodes.BUSINESS_EXCEPTION.getCode(), getMessage("client.error.client_name_exists"));
        }
        clientService.save(clientBo);
        return get(clientBo.getId());
    }

    @RequiresPermissions("update")
    @RequestMapping(value = "/saveForUpdated", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult<ClientBo> saveForUpdate(@Validated @RequestBody ClientBo clientBo) {

        if (clientBo.getId() == null) {
            return JsonResult.fail(ErrorCodes.BUSINESS_EXCEPTION.getCode(), getMessage("common.primary_key.isempty"));
        }
        clientService.save(clientBo);
        return get(clientBo.getId());
    }

    @RequiresPermissions("delete")
    @RequestMapping(value = "/deletes", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult<List<ClientBo>> deletes(@RequestBody Integer[] ids, HttpSession session) {
        if (ids == null || ids.length == 0) {
            return JsonResult.fail(ErrorCodes.BUSINESS_EXCEPTION.getCode(), getMessage("client.msg.select_delete"));
        }
        clientService.deletes(ids);
        return list(session);
    }
    @RequestMapping(value = "/findExistClientName",method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions(value = "view")
    public JsonResult<Boolean> findExistClientName(@RequestBody ClientBo clientBo,HttpSession session){
        boolean flag=true;
        UserResource userResource = (UserResource)session.getAttribute(WebConst.SESSION_RESOURCE);
        if (clientBo.getName()==null||clientBo.getName().equals("")){
            return JsonResult.fail(ErrorCodes.BUSINESS_EXCEPTION.getCode(),userResource.getMessage("client.msg.client_name_exists"));
        }else {
            flag=clientService.findExistClientName(clientBo);
        }
        return JsonResult.success(flag);
    }
}
