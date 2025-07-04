/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.dashboard.controller;

import com.google.common.base.Preconditions;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.dashboard.model.ConnectionInfo;
import org.apache.rocketmq.dashboard.model.request.ConsumerConfigInfo;
import org.apache.rocketmq.dashboard.model.request.DeleteSubGroupRequest;
import org.apache.rocketmq.dashboard.model.request.ResetOffsetRequest;
import org.apache.rocketmq.dashboard.permisssion.Permission;
import org.apache.rocketmq.dashboard.service.ConsumerService;
import org.apache.rocketmq.dashboard.util.JsonUtil;
import org.apache.rocketmq.remoting.protocol.body.ConsumerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/consumer")
@Permission
public class ConsumerController {
    private Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Resource
    private ConsumerService consumerService;

    @RequestMapping(value = "/groupList.query")
    @ResponseBody
    public Object list(@RequestParam(value = "skipSysGroup", required = false) boolean skipSysGroup, String address) {
        return consumerService.queryGroupList(skipSysGroup, address);
    }

    @RequestMapping(value = "/group.refresh")
    @ResponseBody
    public Object refresh(String address,
                          String consumerGroup) {
        return consumerService.refreshGroup(address, consumerGroup);
    }

    @RequestMapping(value = "group.refresh.all")
    @ResponseBody
    public Object refreshAll(String address) {
        return consumerService.refreshAllGroup(address);
    }

    @RequestMapping(value = "/group.query")
    @ResponseBody
    public Object groupQuery(@RequestParam String consumerGroup, String address) {
        return consumerService.queryGroup(consumerGroup, address);
    }

    @RequestMapping(value = "/resetOffset.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object resetOffset(@RequestBody ResetOffsetRequest resetOffsetRequest) {
        logger.info("op=look resetOffsetRequest={}", JsonUtil.obj2String(resetOffsetRequest));
        return consumerService.resetOffset(resetOffsetRequest);
    }

    @RequestMapping(value = "/skipAccumulate.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object skipAccumulate(@RequestBody ResetOffsetRequest resetOffsetRequest) {
        logger.info("op=look resetOffsetRequest={}", JsonUtil.obj2String(resetOffsetRequest));
        return consumerService.resetOffset(resetOffsetRequest);
    }

    @RequestMapping(value = "/examineSubscriptionGroupConfig.query")
    @ResponseBody
    public Object examineSubscriptionGroupConfig(@RequestParam String consumerGroup) {
        return consumerService.examineSubscriptionGroupConfig(consumerGroup);
    }

    @RequestMapping(value = "/deleteSubGroup.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object deleteSubGroup(@RequestBody DeleteSubGroupRequest deleteSubGroupRequest) {
        return consumerService.deleteSubGroup(deleteSubGroupRequest);
    }

    @RequestMapping(value = "/createOrUpdate.do", method = {RequestMethod.POST})
    @ResponseBody
    public Object consumerCreateOrUpdateRequest(@RequestBody ConsumerConfigInfo consumerConfigInfo) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(consumerConfigInfo.getBrokerNameList()) || CollectionUtils.isNotEmpty(consumerConfigInfo.getClusterNameList()),
                "clusterName or brokerName can not be all blank");
        return consumerService.createAndUpdateSubscriptionGroupConfig(consumerConfigInfo);
    }

    @RequestMapping(value = "/fetchBrokerNameList.query", method = {RequestMethod.GET})
    @ResponseBody
    public Object fetchBrokerNameList(@RequestParam String consumerGroup) {
        return consumerService.fetchBrokerNameSetBySubscriptionGroup(consumerGroup);
    }

    @RequestMapping(value = "/queryTopicByConsumer.query")
    @ResponseBody
    public Object queryConsumerByTopic(@RequestParam String consumerGroup, String address) {
        return consumerService.queryConsumeStatsListByGroupName(consumerGroup, address);
    }

    @RequestMapping(value = "/consumerConnection.query")
    @ResponseBody
    public Object consumerConnection(@RequestParam(required = false) String consumerGroup, String address) {
        ConsumerConnection consumerConnection = consumerService.getConsumerConnection(consumerGroup, address);
        consumerConnection.setConnectionSet(ConnectionInfo.buildConnectionInfoHashSet(consumerConnection.getConnectionSet()));
        return consumerConnection;
    }

    @RequestMapping(value = "/consumerRunningInfo.query")
    @ResponseBody
    public Object getConsumerRunningInfo(@RequestParam String consumerGroup, @RequestParam String clientId,
                                         @RequestParam boolean jstack) {
        return consumerService.getConsumerRunningInfo(consumerGroup, clientId, jstack);
    }
}
